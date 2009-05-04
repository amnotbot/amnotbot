package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

public class QurlRequestCommand implements BotCommand
{

    public QurlRequestCommand()
    {       
    }

    public void execute(BotMessage message)
    {
        new QurlRequest(message);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
