package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;


public class YahooNewsSearchCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        new YahooImp(message, YahooImp.searchType.NEWS_SEARCH).run();
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
