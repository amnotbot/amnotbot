package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

public class QuoteCommand extends AmnotbotCommandImp {
	
	public QuoteCommand()
	{
		super("^!quote\\s?(.*)", "quote quotes");
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg) 
	{
		new QuoteThread(con, chan, user, this.getGroup(1));
	}

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
