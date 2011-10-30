package com.github.amnotbot.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class DuckDuckGoSearch 
{

    public enum searchType {
        DEFINITION_SEARCH
    }

    private final String SEARCH_URL =
            "http://api.duckduckgo.com/?format=json&q=";

    public DuckDuckGoSearch()
    {
    }

    public JSONObject search(searchType sType, String query)
            throws MalformedURLException, IOException, JSONException
    {
        URL searchUrl;
        searchUrl = this.buildSearchUrl(sType, query);

        URLConnection googleConn;
        googleConn = this.startConnection(searchUrl);

        return ( this.makeQuery(googleConn) );
    }

    private URL buildSearchUrl(searchType sType, String query)
            throws MalformedURLException, UnsupportedEncodingException 
    {
        String url = null;

        switch (sType) {
            case DEFINITION_SEARCH:
                url = this.SEARCH_URL + "define+";
                break;
        }

        url += URLEncoder.encode(query, "UTF-8");

        return (new URL(url));
    }

    private URLConnection startConnection(URL searchUrl)
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

        return (new JSONObject(builder.toString()));
    }    
}
