package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: gpoppino
 * Date: Oct 27, 2007
 * Time: 3:32:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleYouTubeCommand extends AmnotbotCommandImp {

	public GoogleYouTubeCommand() {
		super("^http://([a-zA-Z]*.)?youtube.com/\\S+", null);
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new GoogleThread(con, chan, user.getNick(), msg);
	}
}

