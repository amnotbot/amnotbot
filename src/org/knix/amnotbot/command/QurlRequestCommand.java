package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;

/**
 * Created by IntelliJ IDEA.
 * User: gresco
 * Date: Oct 20, 2007
 * Time: 5:24:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class QurlRequestCommand extends BotCommandImp {

	public QurlRequestCommand() {
		super("(https?://\\S{75,})", null);
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new QurlRequest(con, chan, user.getNick(), this.getGroup(1));
	}
}
