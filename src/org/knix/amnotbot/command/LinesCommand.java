package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;

public class LinesCommand extends BotCommandImp
{

    public LinesCommand()
    {
        super("^!lines\\s?(.*)", "lines");
    }

    public void execute(BotConnection con, String chan, IRCUser user, String msg)
    {
        new WordsCommandThread(con, chan, user, this.getGroup(1), true);
    }
}
