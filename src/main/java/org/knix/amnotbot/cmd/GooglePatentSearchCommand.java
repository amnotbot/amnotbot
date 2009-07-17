package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GooglePatentSearchCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        new GoogleSearchImp(
                GoogleSearch.searchType.PATENT_SEARCH,
                new GoogleResultOutputPatentStrategy(),
                message
                ).run();
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
