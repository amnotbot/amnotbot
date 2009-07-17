package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.WebPageInfoProxy;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        WebPageInfoProxy webPageInfo;
        webPageInfo = new WebPageInfoProxy(
                message.getText().trim().split("\\s+")[0]);
        String title = webPageInfo.getTitle();
        if (title != null) {
            message.getConn().doPrivmsg(message.getTarget(),
                    "[ " + title + " ]");
        }
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
