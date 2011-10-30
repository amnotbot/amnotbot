package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class DuckDuckGoOutputDefineStrategy implements DuckDuckGoOutputStrategy {

    @Override
    public void showAnswer(BotMessage msg, JSONObject answer) throws Exception 
    {
        String heading = answer.optString("Heading");
        String definition = answer.optString("AbstractText");
        
        if (definition.isEmpty()) {
            msg.getConn().doPrivmsg(msg.getTarget(), "Word not found!");
        } else {
            msg.getConn().doPrivmsg(msg.getTarget(), heading + ": " + definition);
        }
    }
    
}
