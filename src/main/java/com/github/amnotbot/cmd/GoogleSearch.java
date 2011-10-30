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
public class GoogleSearch
{

    public enum searchType {
        BOOKS_SEARCH, NEWS_SEARCH, WEB_SEARCH, PATENT_SEARCH, BLOGS_SEARCH,
        VIDEOS_SEARCH
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

        BotURLConnection conn = new BotURLConnection(searchUrl);

        return ( new JSONObject( conn.fetchURL() ) );
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
            case NEWS_SEARCH:
                url = this.SEARCH_URL + "news?v=1.0&q=";
                break;
            case PATENT_SEARCH:
                url = this.SEARCH_URL + "patent?v=1.0&q=";
                break;
            case BLOGS_SEARCH:
                url = this.SEARCH_URL + "blogs?v=1.0&q=";
                break;
            case VIDEOS_SEARCH:
                url = this.SEARCH_URL + "video?v=1.0&q=";
                break;
        }

        url += URLEncoder.encode(query, "UTF-8");

        return (new URL(url));
    }
}
