package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
class GoogleResultOutputBlogsStrategy implements GoogleResultOutputStrategy
{

    public void showAnswer(BotMessage msg, GoogleResult result) throws Exception
    {
        msg.getConn().doPrivmsg(msg.getTarget(), result.title());
        msg.getConn().doPrivmsg(msg.getTarget(), result.optString("postUrl"));
        msg.getConn().doPrivmsg(msg.getTarget(), result.optString("content"));
        
        String bloginfo;
        bloginfo = "by " +  result.optString("author");
        bloginfo += "; " + result.shortDate("publishedDate");
        msg.getConn().doPrivmsg(msg.getTarget(), bloginfo);
    }

}
