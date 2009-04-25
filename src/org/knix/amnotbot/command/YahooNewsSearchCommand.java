package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;

import com.yahoo.search.SearchClient;

/**
 * Created by IntelliJ IDEA.
 * User: gpoppino
 * Date: Oct 27, 2007
 * Time: 2:13:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class YahooNewsSearchCommand extends BotCommandImp {

	private SearchClient yahooClient;

	public YahooNewsSearchCommand(SearchClient yahooClient) {
		super("^!(y)?news\\s+(.*)", "ynews news");

		this.yahooClient = yahooClient;
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new YahooThread(this.yahooClient, con, chan, user, this.getGroup(2), YahooThread.searchType.NEWS_SEARCH);
	}
}
