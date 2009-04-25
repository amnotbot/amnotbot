package org.knix.amnotbot;

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
public class GoogleSearch
{

    public enum searchType {
        WEB_SEARCH, BOOKS_SEARCH
    }

    private final String SEARCH_URL =
            "http://ajax.googleapis.com/ajax/services/search/";

    public GoogleSearch()
    {
    }

    public JSONObject search(searchType sType, String query)
            throws MalformedURLException, IOException, JSONException
    {
        URL searchUrl;
        searchUrl = this.buildGoogleSearchUrl(sType, query);

        URLConnection googleConn;
        googleConn = this.startGoogleConnection(searchUrl);

        return ( this.makeQuery(googleConn) );
    }

    private URL buildGoogleSearchUrl(searchType sType, String query)
            throws MalformedURLException, UnsupportedEncodingException 
    {
        String url = null;

        switch (sType) {
            case WEB_SEARCH:
                url = this.SEARCH_URL + "web?v=1.0&q=";
                break;
            case BOOKS_SEARCH:
                url = this.SEARCH_URL + "books?v=1.0&q=";
                break;
        }

        url += URLEncoder.encode(query, "UTF-8");

        return (new URL(url));
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
                new InputStreamReader(googleConn.getInputStream()));

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return (new JSONObject(builder.toString()));
    }
}