package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;

public class GoogleWebSearchCommand implements BotCommand
{

    public GoogleWebSearchCommand()
    {
    }

    public void execute(BotMessage message)
    {
        new GoogleSearchImp(
                GoogleSearch.searchType.WEB_SEARCH,
                new GoogleResultOutputWebStrategy(),
                message
                ).run();
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
