package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.WebPageInfoProxy;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoThread extends Thread
{
    private BotMessage msg;
    private WebPageInfoProxy webPageInfo;

    public WebPageInfoThread(BotMessage msg)
    {
        this.msg = msg;

        String url;
        url = msg.getText().trim().split("\\s+")[0];        
        this.webPageInfo = new WebPageInfoProxy(url);        

        start();
    }

    public void run()
    {
        this.showTitle();
    }

    private void showTitle()
    {
        this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                "[ " + this.webPageInfo.getTitle() + " ]");
    }
}
