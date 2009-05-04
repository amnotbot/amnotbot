package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

import com.yahoo.search.SearchClient;

public class YahooWebSearchCommand implements BotCommand
{

    private SearchClient yahooClient;

    public YahooWebSearchCommand(SearchClient yahooClient)
    {       
        this.yahooClient = yahooClient;
    }

    public void execute(BotMessage message)
    {
        new YahooThread(this.yahooClient, message,
                YahooThread.searchType.WEB_SEARCH);
    }

    public String help()
    {
        String msg;

        msg = "Description: Yahoo! search command.";      
        msg += " Parameters: search keywords.";
        msg += " Example: !y airplanes";

        return msg;
    }
}
