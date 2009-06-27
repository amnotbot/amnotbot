package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoCommand implements BotCommand
{
    public WebPageInfoCommand()
    {
    }

    @Override
    public void execute(BotMessage message)
    {
        new WebPageInfoThread(message);
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
