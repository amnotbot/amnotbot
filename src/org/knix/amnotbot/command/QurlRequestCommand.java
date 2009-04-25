package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;

public class QurlRequestCommand extends BotCommandImp
{

    public QurlRequestCommand()
    {
        super("(https?://\\S{75,})", null);
    }

    public void execute(BotConnection con, String chan, IRCUser user, String m)
    {
        new QurlRequest(con, chan, user.getNick(), this.getGroup(1));
    }
}
