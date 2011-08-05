package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
class GoogleResultOutputPatentStrategy implements GoogleResultOutputStrategy
{

    public GoogleResultOutputPatentStrategy()
    {
    }

    public void showAnswer(BotMessage msg, GoogleResult result) throws Exception
    {
        msg.getConn().doPrivmsg(msg.getTarget(), result.title());
        msg.getConn().doPrivmsg(msg.getTarget(), result.decodedUrl("url"));
        msg.getConn().doPrivmsg(msg.getTarget(), result.optString("content"));

        String patentInfo;
        patentInfo = "Patent Number: " + result.optString("patentNumber");
        patentInfo += "; Filing Date: " + result.shortDate("applicationDate");
        patentInfo += "; Status: " + result.optString("patentStatus");
        patentInfo += "; Assignee: " + result.optString("assignee");

        msg.getConn().doPrivmsg(msg.getTarget(), patentInfo);
    }

}
