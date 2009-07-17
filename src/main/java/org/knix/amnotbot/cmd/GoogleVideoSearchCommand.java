package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class GoogleVideoSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        if (message.getText().isEmpty()) return;
        
        new GoogleSearchImp(
                GoogleSearch.searchType.VIDEOS_SEARCH,
                new GoogleResultOutputVideosStrategy(),
                message).run();
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
