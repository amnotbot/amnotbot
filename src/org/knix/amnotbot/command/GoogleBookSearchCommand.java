package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;

public class GoogleBookSearchCommand extends BotCommandImp {

    public GoogleBookSearchCommand() {
        super("^!gbook?\\s+(.*)", "gbook");
    }

    public void execute(BotConnection con, String chan, IRCUser user,
        String msg) {
        new GoogleBookSearchThread(con, chan, user.getNick(), this.getGroup(1));
    }
}
