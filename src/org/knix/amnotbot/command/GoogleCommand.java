package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

public class GoogleCommand implements BotCommand
{

    public GoogleCommand() 
    {
    }

    public void execute(BotMessage message) {
        new GoogleWebSearchThread(message);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
