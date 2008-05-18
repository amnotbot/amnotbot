package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

public class DeliciousCommand extends AmnotbotCommandImp
{
	private DeliciousBookmarks delicious;
	private boolean showURL;

	public DeliciousCommand(boolean showURL) {
		super("^((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)", "delicious");
		this.delicious = new DeliciousBookmarks();
		this.showURL = showURL;
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new DeliciousThread(this.delicious,
				con, chan, user.getNick(), this.getGroup(2).split(" ")[0], msg, this.showURL);
	}

	public String help()
	{
		String msg;

		msg = "del.icio.us social bookmarking! ";
		msg += "Options: [ title:\"your title\" ";
		msg += "tags:abc,secondtag,thirdtag,def ";
		msg += "comment:\"your comment\" ] ";

		return msg;
	}
}
