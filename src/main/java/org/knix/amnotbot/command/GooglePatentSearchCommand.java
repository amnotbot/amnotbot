package org.knix.amnotbot.command;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GooglePatentSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new GoogleSearchThread(
                GoogleSearch.searchType.PATENT_SEARCH,
                new GoogleResultOutputPatentStrategy(),
                message
                );
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
