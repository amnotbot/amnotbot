package org.knix.amnotbot;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.schwering.irc.lib.IRCUser;


public class HelpCommand extends AmnotbotCommandImp {
	
	private LinkedList<AmnotbotCommand> cmds;
	
	public HelpCommand()
	{
		super("^!help\\s?(.*)", "help");

		this.cmds = new LinkedList<AmnotbotCommand>();
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg) 
	{
		boolean found = false;
		Iterator<AmnotbotCommand> it = this.cmds.iterator();
                while (it.hasNext()) {
                        AmnotbotCommand command = it.next();

			String keywords = command.getKeywords();

			if (keywords == null)
				continue;

			String [] k = keywords.split(" ");
			String regexp = "(" + k[0];
			for (int i = 1; i < k.length; ++i) {
				regexp += "|";
				regexp += k[i];
			}
			regexp += ")";

			Matcher m = Pattern.compile(
					regexp, 
					Pattern.CASE_INSENSITIVE).matcher(this.getGroup(1));

			if (m.find()) {
				String helpMsg = command.help();

				BotLogger.getDebugLogger().debug("Help " + helpMsg);

				if (helpMsg != null)
					con.doPrivmsg(chan, helpMsg);
				else 
					con.doPrivmsg(chan, this.noHelp());

				found = true;

				break;
			}
                }

		if (!found)
			con.doPrivmsg(chan, this.help());
	}


	public void addCommands(LinkedList<AmnotbotCommand> l)
	{
		this.cmds.addAll(l);
	}

	public String noHelp()
	{
		String msg;

		msg = "No help available for command";

		return msg;
	}

	public String help()
	{
		String msg = null;

		Iterator<AmnotbotCommand> it = this.cmds.iterator();
                while (it.hasNext()) {
                        AmnotbotCommand command = it.next();

			String keywords = command.getKeywords();

			if (keywords == null)
				continue;

			if (msg == null)
				msg = "Available commands: " + keywords;
			else
				msg += " " + keywords;
		}

		return msg;
	}
}
