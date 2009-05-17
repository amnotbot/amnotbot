package org.knix.amnotbot.command;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GoogleBlogsSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new GoogleSearchThread(
                GoogleSearch.searchType.BLOGS_SEARCH,
                new GoogleResultOutputBlogsStrategy(),
                message);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
