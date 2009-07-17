package org.knix.amnotbot.cmd;

import org.apache.commons.lang.SystemUtils;
import org.knix.amnotbot.*;

public class QuoteCommand implements BotCommand
{
    String dbFilename;

    public QuoteCommand()
    {
        this.dbFilename = SystemUtils.getUserHome() + "/" + ".amnotbot" + "/" +
                "quotes.db";
    }

    @Override
    public void execute(BotMessage message)
    {
        new QuoteImp(this.dbFilename, message).run();
    }

    @Override
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
