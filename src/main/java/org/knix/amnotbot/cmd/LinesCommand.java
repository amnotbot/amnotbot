package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;

public class LinesCommand implements BotCommand
{

    public LinesCommand()
    {
    }

    public void execute(BotMessage message)
    {
        new WordsCommandThread(message,
                WordsCommandThread.countOperation.LINES);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
