package org.knix.amnotbot.command;

import org.knix.amnotbot.command.utils.CommandOptions;
import org.knix.amnotbot.command.utils.CmdStringOption;
import org.knix.amnotbot.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.schwering.irc.lib.IRCUser;

import com.yahoo.search.NewsSearchRequest;
import com.yahoo.search.NewsSearchResult;
import com.yahoo.search.NewsSearchResults;
import com.yahoo.search.SearchClient;
import com.yahoo.search.SearchException;
import com.yahoo.search.WebSearchRequest;
import com.yahoo.search.WebSearchResult;
import com.yahoo.search.WebSearchResults;

public class YahooThread extends Thread
{

    SearchClient yahooClient;
    BotConnection con;
    String chan;
    IRCUser user;
    String msg;
    String query;
    CommandOptions opts;

    public enum searchType
    {
        WEB_SEARCH, NEWS_SEARCH
    }
    searchType sType;

    public YahooThread(SearchClient yahooClient,
            BotConnection con,
            String chan,
            IRCUser user,
            String msg,
            searchType sType)
    {
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

        if (this.opts.hasOptions()) {
            this.query = this.msg.substring(0, this.opts.optionStartAt());
        } else {
            this.query = this.msg;
        }

        BotLogger.getDebugLogger().debug("query:" + this.query);

        switch (this.sType) {
            case NEWS_SEARCH:
                this.newsSearch();
                break;
            case WEB_SEARCH:
                this.webSearch();
                break;
        }
    }

    private WebSearchRequest prepareWebSearchRequest(String query)
    {
        WebSearchRequest request = new WebSearchRequest(this.query);

        request.setResults(1);

        if (this.opts.getOption("language").hasValue()) {
            request.setLanguage(this.opts.getOption("language").stringValue());
        } else {
            request.setLanguage("en");
        }

        if (this.opts.getOption("country").hasValue()) {
            request.setCountry(this.opts.getOption("country").stringValue());
        }

        if (this.opts.getOption("format").hasValue()) {
            request.setFormat(this.opts.getOption("format").stringValue());
        }

        if (this.opts.getOption("region").hasValue()) {
            request.setRegion(this.opts.getOption("region").stringValue());
        }

        if (this.opts.getOption("adult_ok").hasValue()) {
            String adult_ok = this.opts.getOption("adult_ok").stringValue();
            String _yes = new String("yes");
            if (adult_ok.equals(_yes)) {
                request.setAdultOk(true);
            } else {
                request.setAdultOk(false);
            }
        }

        if (this.opts.getOption("type").hasValue()) {
            request.setType(this.opts.getOption("type").stringValue());
        }

        if (this.opts.getOption("license").hasValue()) {
            request.addLicense(this.opts.getOption("license").stringValue());
        }

        if (this.opts.getOption("site").hasValue()) {
            request.addSite(this.opts.getOption("site").stringValue());
        }

        BotLogger.getDebugLogger().debug("parameters:" +
                request.getParameters());

        return request;
    }

    private void webSearch()
    {
        WebSearchRequest request = this.prepareWebSearchRequest(this.query);

        try {
            WebSearchResults results = this.yahooClient.webSearch(request);

            BotLogger.getDebugLogger().debug("Found " +
                    results.getTotalResultsAvailable() +
                    " hits for " + this.msg + "! Displaying the first " +
                    results.getTotalResultsReturned() + ".");
            BotLogger.getDebugLogger().debug("Performed search:" + this.msg);

            if (results.listResults().length != 0) {
                WebSearchResult result = results.listResults()[0];
                
                BotLogger.getDebugLogger().debug("  : " + result.getTitle() + 
                        " - " + result.getUrl());
                this.con.doPrivmsg(this.chan, result.getTitle());
                this.con.doPrivmsg(this.chan, result.getUrl());
                this.con.doPrivmsg(this.chan, result.getSummary());
            } else {
                this.con.doPrivmsg(this.chan, "Nothing found.");
            }
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug("Error calling Yahoo! : " +
                    e.toString());
        } catch (SearchException e) {
            BotLogger.getDebugLogger().debug("Error calling Yahoo! : " +
                    e.toString());
            this.con.doPrivmsg(this.chan, "Invalid parameters! Check: " +
                    "http://developer.yahoo.com/search/web/V1/webSearch.html");
        }
    }

    private NewsSearchRequest prepareNewsSearchRequest(String query)
    {
        NewsSearchRequest request = new NewsSearchRequest(query);

        request.setResults(1);
        if (this.opts.getOption("language").hasValue()) {
            request.setLanguage(this.opts.getOption("language").stringValue());
        } else {
            request.setLanguage("en");
        }

        BotLogger.getDebugLogger().debug("parameters:" +
                request.getParameters());

        return request;
    }

    private void newsSearch()
    {
        NewsSearchRequest request = this.prepareNewsSearchRequest(this.query);

        try {
            NewsSearchResults results = this.yahooClient.newsSearch(request);

            BotLogger.getDebugLogger().debug("Found " +
                    results.getTotalResultsAvailable() +
                    " hits for " + this.msg + "! Displaying the first " +
                    results.getTotalResultsReturned() + ".");
            BotLogger.getDebugLogger().debug("Performed search:" + this.msg);
            
            if (results.listResults().length != 0) {
                NewsSearchResult result = results.listResults()[0];

                BotLogger.getDebugLogger().debug("  : " + result.getTitle() + 
                        " - " + result.getUrl());

                SimpleDateFormat publishDate =
                        new SimpleDateFormat("MMM dd, yyyy");
                Long ts = new Long(result.getPublishDate());
                long timestamp = ts.longValue() * 1000;
                String mDate = publishDate.format(new Date(timestamp));

                this.con.doPrivmsg(this.chan, result.getTitle() + " - " +
                        result.getNewsSource() + " - " + mDate);
                this.con.doPrivmsg(this.chan, result.getUrl());
                this.con.doPrivmsg(this.chan, result.getSummary());
            } else {
                this.con.doPrivmsg(this.chan, "Nothing found.");
            }
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug("Error calling Yahoo! : " +
                    e.toString());
        } catch (SearchException e) {
            BotLogger.getDebugLogger().debug("Error calling Yahoo! :" +
                    e.toString());
        }
    }
}