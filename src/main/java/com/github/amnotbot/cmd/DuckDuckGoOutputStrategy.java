package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public interface DuckDuckGoOutputStrategy {
        
    public void showAnswer(BotMessage msg,  JSONObject answer)
            throws Exception;
}
