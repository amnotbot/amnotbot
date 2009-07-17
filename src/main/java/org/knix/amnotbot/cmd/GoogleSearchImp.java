package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;

import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleSearchImp
{

    private BotMessage msg;
    private GoogleSearch.searchType sType;
    private GoogleResultOutputStrategy outputStrategy;

    public GoogleSearchImp(
            GoogleSearch.searchType s,
            GoogleResultOutputStrategy outputStrategy,
            BotMessage msg)
    {
        this.msg = msg;
        this.sType = s;
        this.outputStrategy = outputStrategy;
    }

    public void run()
    {
        try {
            GoogleSearch google = new GoogleSearch();

            JSONObject answer;
            answer = google.search(this.sType, this.msg.getText());
            this.outputStrategy.showAnswer(this.msg, new GoogleResult(answer));
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }
}
