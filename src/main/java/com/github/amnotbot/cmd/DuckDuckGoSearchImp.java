package com.github.amnotbot.cmd;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotMessage;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class DuckDuckGoSearchImp {
    
    private BotMessage msg;
    private DuckDuckGoSearch.searchType sType;
    private DuckDuckGoOutputStrategy outputStrategy;

    public DuckDuckGoSearchImp(
            DuckDuckGoSearch.searchType s,
            DuckDuckGoOutputStrategy outputStrategy,
            BotMessage msg)
    {
        this.msg = msg;
        this.sType = s;
        this.outputStrategy = outputStrategy;
    }

    public void run()
    {
        try {
            DuckDuckGoSearch duckduckgo = new DuckDuckGoSearch();

            JSONObject answer;
            answer = duckduckgo.search(this.sType, this.msg.getText());
            this.outputStrategy.showAnswer(this.msg, answer);
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }
}
