package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

public class GoogleBookSearchCommand implements BotCommand
{
  
    public void execute(BotMessage message)
    {
        new GoogleSearchThread(
                GoogleSearch.searchType.BOOKS_SEARCH,
                new GoogleResultOutputBooksStrategy(),
                message);        
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
