package org.knix.amnotbot.command;

import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
class GoogleResultOutputVideosStrategy implements GoogleResultOutputStrategy
{

    public void showAnswer(BotMessage msg, GoogleResult result) throws Exception
    {
        msg.getConn().doPrivmsg(msg.getTarget(), result.title());
        msg.getConn().doPrivmsg(msg.getTarget(), result.decodedUrl("url"));
        msg.getConn().doPrivmsg(msg.getTarget(), result.optString("content"));

        String videoInfo = new String();
        String author = result.optString("author");
        if (!author.isEmpty()) {
            videoInfo = "by " +  author + "; ";
        }
        Float rating = new Float( result.optString("rating") );
        if (rating <= 0) {
            videoInfo += "No rating; ";
        } else {
            videoInfo += Math.round(rating.floatValue() * 100.0) / 100.0 +
                    " stars; ";
        }                    
        String viewCount = result.optString("viewCount");
        if (!viewCount.isEmpty()) {
            videoInfo += viewCount + " views; " ;
        }
        int duration = new Integer(result.optString("duration")).intValue();
        String ds = "sec";
        if ((duration / 60) > 0) {
             ds = "min";
        }
        videoInfo += duration / 60 + ":" + duration % 60 + " " + ds + "; ";
        videoInfo += result.shortDate("published");
        msg.getConn().doPrivmsg(msg.getTarget(), videoInfo);
    }

}
