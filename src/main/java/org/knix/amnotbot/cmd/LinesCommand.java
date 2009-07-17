package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;

public class LinesCommand implements BotCommand
{

    public LinesCommand()
    {
    }

    @Override
    public void execute(BotMessage message)
    {
        new WordsCommandImp(message,
                WordsCommandImp.countOperation.LINES).run();
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
