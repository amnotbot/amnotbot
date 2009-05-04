package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

public class GoogleBookSearchCommand implements BotCommand
{
  
    public void execute(BotMessage message)
    {
        new GoogleBookSearchThread(message);
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
