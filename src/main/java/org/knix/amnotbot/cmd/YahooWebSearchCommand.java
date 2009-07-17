package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;


public class YahooWebSearchCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        new YahooImp(message, YahooImp.searchType.WEB_SEARCH).run();
    }

    @Override
    public String help()
    {
        String msg;

        msg = "Description: Yahoo! search command.";      
        msg += " Parameters: search keywords.";
        msg += " Example: !y airplanes";

        return msg;
    }
}
