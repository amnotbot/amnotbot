package org.knix.amnotbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.schwering.irc.lib.IRCUser;

public class WordsCommand extends AmnotbotCommandImp {
	
	public WordsCommand()
	{
		super("^!(w(ords)?)\\s?(.*)", "w words");
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg) 
	{
		new WordsCommandThread(con, chan, user, this.getGroup(3), false);
	}
}
