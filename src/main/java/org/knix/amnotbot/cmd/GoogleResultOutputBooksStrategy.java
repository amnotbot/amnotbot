package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
class GoogleResultOutputBooksStrategy implements GoogleResultOutputStrategy
{

    public void showAnswer(BotMessage msg, GoogleResult result) throws Exception
    {
        msg.getConn().doPrivmsg(msg.getTarget(), result.title());
        msg.getConn().doPrivmsg(msg.getTarget(), result.decodedUrl("url"));

        String bookInfo;
        bookInfo = "by " + result.optString("authors");
        bookInfo += " - " + result.optString("bookId");
        bookInfo += " - " + result.optString("publishedYear");
        bookInfo += " - " + result.optString("pageCount");
        bookInfo += " pages";

        msg.getConn().doPrivmsg(msg.getTarget(), bookInfo);
    }

}
