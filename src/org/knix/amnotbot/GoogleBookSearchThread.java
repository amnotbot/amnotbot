package org.knix.amnotbot;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleBookSearchThread extends Thread {

    private BotConnection con;
    private String query;
    private String chan;
    private String nick;

    public GoogleBookSearchThread(BotConnection con,
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
            JSONObject answer;
            GoogleSearch google = new GoogleSearch();

            answer = google.search(GoogleSearch.searchType.BOOKS_SEARCH,
                    this.query);

            this.showAnswer(answer);
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
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

        String bookInfo;
        bookInfo = "by " + result.optString("authors");
        bookInfo += " - " + result.optString("bookId");
        bookInfo += " - " + result.optString("publishedYear");
        bookInfo += " - " + result.optString("pageCount");
        bookInfo += " pages";

        this.con.doPrivmsg(this.chan, bookInfo);
    }
}