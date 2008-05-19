package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;


/**
 * Created by IntelliJ IDEA.
 * User: gresco
 * Date: Oct 20, 2007
 * Time: 2:43:47 PM
 */
public interface AmnotbotCommand {

	public boolean matches(String msg);
	public void execute(BotConnection con, String chan, IRCUser user, String msg);

	public String getKeywords();
	public void setKeywords(String keywords);

	public String help(); 
}
