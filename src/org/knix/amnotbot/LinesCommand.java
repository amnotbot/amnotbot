package org.knix.amnotbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.schwering.irc.lib.IRCUser;

public class LinesCommand extends AmnotbotCommandImp {
	
	public LinesCommand() {
		super("^!lines\\s?(.*)", "lines");
	}
	

	public void execute(BotConnection con, String chan, IRCUser user, String msg) 
	{
		new WordsCommandThread(con, chan, user, this.getGroup(1), true);
	}
}
