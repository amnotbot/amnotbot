package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;


public class YahooNewsSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new YahooThread(message, YahooThread.searchType.NEWS_SEARCH);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
