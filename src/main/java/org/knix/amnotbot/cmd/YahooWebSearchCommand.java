package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;


public class YahooWebSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new YahooThread(message, YahooThread.searchType.WEB_SEARCH);
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
