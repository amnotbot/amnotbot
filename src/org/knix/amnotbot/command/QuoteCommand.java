package org.knix.amnotbot.command;

import org.knix.amnotbot.*;

public class QuoteCommand implements BotCommand
{

    public QuoteCommand()
    {
    }

    public void execute(BotMessage message)
    {
        new QuoteThread(message);
    }

    public String help()
    {
        String msg;

        msg = "Description: Quotes command.";
        msg += " Keywords: quote quotes.";
        msg += " Parameters: op:set text:\"Your text\"";
        msg += " | ";
        msg += "op:del id:number";
        msg += " | ";
        msg += "op:info id:number";
        msg += " | ";
        msg += "op:get";

        return msg;
    }
}
