package org.knix.amnotbot.command;

import java.io.UnsupportedEncodingException;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GoogleResultOutputWebStrategy implements GoogleResultOutputStrategy
{

    public void showAnswer(BotMessage msg, GoogleResult result)
            throws UnsupportedEncodingException
    {        
        msg.getConn().doPrivmsg(msg.getTarget(), result.title());
        msg.getConn().doPrivmsg(msg.getTarget(), result.decodedUrl("url"));
        msg.getConn().doPrivmsg(msg.getTarget(), result.optString("content"));
    }
}
