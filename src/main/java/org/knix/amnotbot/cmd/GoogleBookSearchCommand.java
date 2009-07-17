package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;

public class GoogleBookSearchCommand implements BotCommand
{
  
    @Override
    public void execute(BotMessage message)
    {
        new GoogleSearchImp(
                GoogleSearch.searchType.BOOKS_SEARCH,
                new GoogleResultOutputBooksStrategy(),
                message).run();
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
