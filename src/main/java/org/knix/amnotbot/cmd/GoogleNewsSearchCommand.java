package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GoogleNewsSearchCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        new GoogleSearchImp(
                GoogleSearch.searchType.NEWS_SEARCH,
                new GoogleResultOutputWebStrategy(),
                message).run();
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
