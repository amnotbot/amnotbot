package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.htmlparser.util.ParserUtils;
import org.htmlparser.util.Translate;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleWebSearchThread extends Thread
{
    private BotMessage msg;
    private GoogleSearch.searchType sType;

    public GoogleWebSearchThread(GoogleSearch.searchType s, BotMessage msg)
    {
        this.msg = msg;
        this.sType = s;

        start();
    }

    public void run()
    {
        try {          
            GoogleSearch google = new GoogleSearch();
            
            JSONObject answer;
            answer = google.search(this.sType, this.msg.getText());
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
        this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                Translate.decode(title)
                );

        String url = URLDecoder.decode(result.optString("url"), "UTF-8");
        this.msg.getConn().doPrivmsg(this.msg.getTarget(), url);

        String snippet = Translate.decode( result.optString("content") );
        this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                ParserUtils.trimAllTags(snippet, false)
                );
    }
}
