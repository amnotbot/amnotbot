package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

import com.yahoo.search.SearchClient;

public class YahooNewsSearchCommand implements BotCommand
{

    private SearchClient yahooClient;

    public YahooNewsSearchCommand(SearchClient yahooClient)
    {   
        this.yahooClient = yahooClient;
    }

    public void execute(BotMessage message)
    {
        new YahooThread(this.yahooClient, message,
                YahooThread.searchType.NEWS_SEARCH);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
