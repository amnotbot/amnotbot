package org.knix.amnotbot;

import com.yahoo.search.*;
import org.schwering.irc.lib.IRCUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: gpoppino
 * Date: Oct 25, 2007
 * Time: 12:12:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class YahooThread extends Thread {

	SearchClient yahooClient;
	BotConnection con;
	String chan;
	IRCUser user;
	String msg;
	String query;
	CommandOptions opts;

	public enum searchType { WEB_SEARCH, NEWS_SEARCH }

	searchType sType;

	public YahooThread(SearchClient yahooClient, BotConnection con, String chan, IRCUser user, String msg, searchType sType) {
		this.yahooClient = yahooClient;
		this.con = con;
		this.chan = chan;
		this.user = user;
		this.msg = msg;
		this.sType = sType;
		
		this.opts = new CommandOptions(msg);
		
		this.opts.addOption(new CmdStringOption("region"));
		this.opts.addOption(new CmdStringOption("format"));
		this.opts.addOption(new CmdStringOption("language"));
		this.opts.addOption(new CmdStringOption("country"));
		this.opts.addOption(new CmdStringOption("site"));
		this.opts.addOption(new CmdStringOption("adult_ok"));
		this.opts.addOption(new CmdStringOption("license"));
		this.opts.addOption(new CmdStringOption("type"));
		
		start();
	}

	public void run()
	{
		this.opts.buildArgs();
		
		if (this.opts.hasOptions())
			this.query = this.msg.substring(0, this.opts.optionStartAt());
		else
			this.query = this.msg;
		
		System.out.println("query:" + this.query);
		
		switch (this.sType) {
			case NEWS_SEARCH:
				this.newsSearch();
				break;
			case WEB_SEARCH:
				this.webSearch();
				break;
		}		
	}

	private void webSearch()
	{
		WebSearchRequest request = new WebSearchRequest(this.query);
						
		request.setResults(1);
		
		if (this.opts.getOption("language").hasValue())
			request.setLanguage(this.opts.getOption("language").stringValue());
		else
			request.setLanguage("en");
			
		if (this.opts.getOption("country").hasValue())
			request.setCountry(this.opts.getOption("country").stringValue());
		
		if (this.opts.getOption("format").hasValue())
			request.setFormat(this.opts.getOption("format").stringValue());
		
		if (this.opts.getOption("region").hasValue())
			request.setRegion(this.opts.getOption("region").stringValue());
		
		if (this.opts.getOption("adult_ok").hasValue()) {
			String adult_ok = this.opts.getOption("adult_ok").stringValue();
			if (adult_ok == "yes")
				request.setAdultOk(true);
			else
				request.setAdultOk(false);
		}
		
		if (this.opts.getOption("type").hasValue())
			request.setType(this.opts.getOption("type").stringValue());
		
		if (this.opts.getOption("license").hasValue())
			request.addLicense(this.opts.getOption("license").stringValue());
		
		if (this.opts.getOption("site").hasValue())
			request.addSite(this.opts.getOption("site").stringValue());
		
		System.out.println("parameters:" + request.getParameters());
		
		try {
			// Execute the search.
			WebSearchResults results = this.yahooClient.webSearch(request);			

			// Print out how many hits were found.
			System.out.println("Found " + results.getTotalResultsAvailable() +
				" hits for " + this.msg + "! Displaying the first " +
				results.getTotalResultsReturned() + ".");
			System.out.println("Performed search:" + this.msg);

			// Iterate over the results.
			//for (int i = 0; i < results.listResults().length; i++) {

			//WebSearchResult result = results.listResults()[i];
			if (results.listResults().length != 0) {				
				//BigInteger firstPos = results.getFirstResultPosition();
				//System.out.println("First position: " + firstPos.toString());
				WebSearchResult result = results.listResults()[0];				

				// Print out the document title and URL.
				System.out.println("  : " + result.getTitle() + " - " +
					result.getUrl());
				this.con.doPrivmsg(this.chan, result.getTitle());
				this.con.doPrivmsg(this.chan, result.getUrl());
				this.con.doPrivmsg(this.chan, result.getSummary());
			} else {
				this.con.doPrivmsg(this.chan, "Nothing found.");
			}
		}
		catch (IOException e) {
			// Most likely a network exception of some sort.
			System.err.println("Error calling Yahoo! Search Service: " +
				e.toString());
			e.printStackTrace(System.err);
		}
		catch (SearchException e) {
			// An issue with the XML or with the service.
			System.err.println("Error calling Yahoo! Search Service: " +
				e.toString());
			e.printStackTrace(System.err);
			this.con.doPrivmsg(this.chan, "Invalid parameters! Check: http://developer.yahoo.com/search/web/V1/webSearch.html");
		}
	}

	private void newsSearch()
	{
		NewsSearchRequest request = new NewsSearchRequest(this.query);

		request.setResults(1);
		if (this.opts.getOption("language").hasValue())
			request.setLanguage(this.opts.getOption("language").stringValue());
		else
			request.setLanguage("en");
		
		System.out.println("parameters:" + request.getParameters());

		try {
			// Execute the search.
			NewsSearchResults results = this.yahooClient.newsSearch(request);		

			// Print out how many hits were found.
			System.out.println("Found " + results.getTotalResultsAvailable() +
				" hits for " + this.msg + "! Displaying the first " +
				results.getTotalResultsReturned() + ".");
			System.out.println("Performed search:" + this.msg);

			// Iterate over the results.
			//for (int i = 0; i < results.listResults().length; i++) {

			//WebSearchResult result = results.listResults()[i];
			if (results.listResults().length != 0) {
				//BigInteger firstPos = results.getFirstResultPosition();
				//System.out.println("First position: " + firstPos.toString());
				NewsSearchResult result = results.listResults()[0];

				// Print out the document title and URL.
				System.out.println("  : " + result.getTitle() + " - " +
					result.getUrl());
		
				SimpleDateFormat publishDate = new SimpleDateFormat("MMM dd, yyyy");
				Long ts = new Long( result.getPublishDate() );
				long timestamp =  ts.longValue() * 1000;
				String mDate = publishDate.format( new Date(timestamp) );
				
				this.con.doPrivmsg(this.chan, result.getTitle() + " - " + result.getNewsSource() + " - " + mDate);
				this.con.doPrivmsg(this.chan, result.getUrl());
				this.con.doPrivmsg(this.chan, result.getSummary());
			} else {
				this.con.doPrivmsg(this.chan, "Nothing found.");
			}
		}
		catch (IOException e) {
			// Most likely a network exception of some sort.
			System.err.println("Error calling Yahoo! Search Service: " +
				e.toString());
			e.printStackTrace(System.err);
		}
		catch (SearchException e) {
			// An issue with the XML or with the service.
			System.err.println("Error calling Yahoo! Search Service: " +
				e.toString());
			e.printStackTrace(System.err);
		}
	}
}
