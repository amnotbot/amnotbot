package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

public class GoogleBookSearchCommand extends AmnotbotCommandImp {

	public GoogleBookSearchCommand() {
		super("^!gbook?\\s+(.*)", null);
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new GoogleBookSearchThread(con, chan, user.getNick(), this.getGroup(1));
	}
}
