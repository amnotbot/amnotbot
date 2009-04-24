package org.knix.amnotbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.htmlparser.util.ParserUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleWebSearchThread extends Thread {

    private BotConnection con;
    private String query;
    private String chan;
    private String nick;

    public GoogleWebSearchThread(BotConnection con,
            String chan,
            String nick,
            String query) {
        this.con = con;
        this.chan = chan;
        this.nick = nick;
        this.query = query;

        start();
    }

    public void run()
    {
        try {
            URL searchUrl;
            searchUrl = this.buildGoogleSearchUrl(this.query);

            URLConnection googleConn;
            googleConn = this.startGoogleConnection(searchUrl);

            JSONObject answer;
            answer = this.makeQuery(googleConn);

            this.showAnswer(answer);
         } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
         }
    }

    private URL buildGoogleSearchUrl(String query)
            throws MalformedURLException, UnsupportedEncodingException
    {        
        String urlString =
            "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q="
                + URLEncoder.encode(query, "UTF-8");

        return ( new URL(urlString) );
    }

    private URLConnection startGoogleConnection(URL searchUrl)
            throws IOException
    {
        URLConnection googleConn;
        
        googleConn = searchUrl.openConnection();        
        googleConn.addRequestProperty("Referer", "http://packetpan.org");
        
        return googleConn;
    }

    public JSONObject makeQuery(URLConnection googleConn)
            throws IOException, JSONException
    {       
        BufferedReader reader;
        reader = new BufferedReader(
                    new InputStreamReader(googleConn.getInputStream())
                    );

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
     
        return ( new JSONObject( builder.toString() ) );
    }

    private void showAnswer(JSONObject answer) 
            throws JSONException, UnsupportedEncodingException
    {
        JSONObject data = answer.getJSONObject("responseData");
        JSONObject result = data.getJSONArray("results").getJSONObject(0);

        String title = result.optString("titleNoFormatting");
        this.con.doPrivmsg(this.chan, title);

        String url = URLDecoder.decode(result.optString("url"), "UTF-8");
        this.con.doPrivmsg(this.chan, url);

        String snippet = result.optString("content");
        this.con.doPrivmsg(this.chan,
                ParserUtils.trimAllTags(snippet, false));
    }
}