package org.knix.amnotbot;

import com.yahoo.search.SearchClient;
import org.schwering.irc.lib.IRCUser;

/**
 * Created by IntelliJ IDEA.
 * User: gpoppino
 * Date: Oct 25, 2007
 * Time: 12:01:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class YahooWebSearchCommand extends AmnotbotCommandImp {

	private SearchClient yahooClient;

	public YahooWebSearchCommand(SearchClient yahooClient) {
		super("^!y(ahoo)?\\s+(.*)", "y yahoo");

		this.yahooClient = yahooClient;
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new YahooThread(this.yahooClient, con, chan, user, this.getGroup(2), YahooThread.searchType.WEB_SEARCH);
	}

	public String help()
	{
		String msg;

		msg = "Description: Yahoo! search command.";
		msg += " Keywords: " + this.getKeywords() + ".";
		msg += " Parameters: search keywords.";
		msg += " Example: !y airplanes";

		return msg;
	}
}
