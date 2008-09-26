package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

public class GoogleCommand extends AmnotbotCommandImp {

    public GoogleCommand() {
        super("^!g(oogle)?\\s+(.*)", "g google");
    }

    public void execute(BotConnection con, String chan, IRCUser user,
        String msg) {
        new GoogleWebSearchThread(con, chan, user.getNick(), this.getGroup(2));
    }
}
