package org.knix.amnotbot.command;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GoogleNewsSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new GoogleWebSearchThread(GoogleSearch.searchType.NEWS_SEARCH, message);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
