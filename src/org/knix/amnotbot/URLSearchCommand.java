package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

import com.yahoo.search.SearchClient;

/**
 * Created by IntelliJ IDEA.
 * User: gpoppino
 * Date: Nov 3, 2007
 * Time: 2:04:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLSearchCommand extends AmnotbotCommandImp {

	private SearchClient yahooClient;

	public URLSearchCommand(SearchClient yahooClient) {
		super("!more\\s+(https?://.*)\\s*", "more");

		this.yahooClient = yahooClient;		
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		new YahooThread(this.yahooClient, con, chan, user, this.getGroup(1), YahooThread.searchType.WEB_SEARCH);
	}
}
