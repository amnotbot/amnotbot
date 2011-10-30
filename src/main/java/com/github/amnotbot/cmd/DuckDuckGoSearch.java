package com.github.amnotbot.cmd;

import com.github.amnotbot.cmd.utils.BotURLConnection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
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

        BotURLConnection conn = new BotURLConnection(searchUrl);

        return ( new JSONObject( conn.fetchURL() ) );
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
}
