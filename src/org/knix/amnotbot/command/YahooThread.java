package org.knix.amnotbot.command;

import org.knix.amnotbot.command.utils.CommandOptions;
import org.knix.amnotbot.command.utils.CmdStringOption;
import org.knix.amnotbot.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    String query;
    BotMessage msg;
    CommandOptions opts;
    SearchClient yahooClient;    

    public enum searchType
    {
        WEB_SEARCH, NEWS_SEARCH
    }
    searchType sType;

    public YahooThread(SearchClient yahooClient,
            BotMessage msg,
            searchType sType)
    {
        this.yahooClient = yahooClient;
        this.msg = msg;
        this.sType = sType;

        this.opts = new CommandOptions(msg.getText());

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

        String str = this.msg.getText();
        if (this.opts.hasOptions()) {
            this.query = str.substring(0, this.opts.optionStartAt());
        } else {
            this.query = str;
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
            request.setLanguage(this.opts.getOption("language").tokens()[0]);
        } else {
            request.setLanguage("en");
        }

        if (this.opts.getOption("country").hasValue()) {
            request.setCountry(this.opts.getOption("country").tokens()[0]);
        }

        if (this.opts.getOption("format").hasValue()) {
            request.setFormat(this.opts.getOption("format").tokens()[0]);
        }

        if (this.opts.getOption("region").hasValue()) {
            request.setRegion(this.opts.getOption("region").tokens()[0]);
        }

        if (this.opts.getOption("adult_ok").hasValue()) {
            String adult_ok = this.opts.getOption("adult_ok").tokens()[0];
            String _yes = new String("yes");
            if (adult_ok.equals(_yes)) {
                request.setAdultOk(true);
            } else {
                request.setAdultOk(false);
            }
        }

        if (this.opts.getOption("type").hasValue()) {
            request.setType(this.opts.getOption("type").tokens()[0]);
        }

        if (this.opts.getOption("license").hasValue()) {
            request.addLicense(this.opts.getOption("license").tokens()[0]);
        }

        if (this.opts.getOption("site").hasValue()) {
            request.addSite(this.opts.getOption("site").tokens()[0]);
        }
        return request;
    }

    private void webSearch()
    {
        WebSearchRequest request = this.prepareWebSearchRequest(this.query);

        try {
            WebSearchResults results = this.yahooClient.webSearch(request);

            if (results.listResults().length != 0) {
                WebSearchResult result = results.listResults()[0];
                
                BotLogger.getDebugLogger().debug("  : " + result.getTitle() + 
                        " - " + result.getUrl());
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        result.getTitle());
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        result.getUrl());
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        result.getSummary());
            } else {
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        "Nothing found.");
            }
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug("Error calling Yahoo! : " +
                    e.toString());
        } catch (SearchException e) {
            BotLogger.getDebugLogger().debug("Error calling Yahoo! : " +
                    e);
            this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                    "Invalid parameters! Check: " +
                    "http://developer.yahoo.com/search/web/V1/webSearch.html");
        }
    }

    private NewsSearchRequest prepareNewsSearchRequest(String query)
    {
        NewsSearchRequest request = new NewsSearchRequest(query);

        request.setResults(1);
        if (this.opts.getOption("language").hasValue()) {
            request.setLanguage(this.opts.getOption("language").tokens()[0]);
        } else {
            request.setLanguage("en");
        }
        return request;
    }

    private void newsSearch()
    {
        NewsSearchRequest request = this.prepareNewsSearchRequest(this.query);

        try {
            NewsSearchResults results = this.yahooClient.newsSearch(request);
            
            if (results.listResults().length != 0) {
                NewsSearchResult result = results.listResults()[0];

                SimpleDateFormat publishDate =
                        new SimpleDateFormat("MMM dd, yyyy");
                Long ts = new Long(result.getPublishDate());
                long timestamp = ts.longValue() * 1000;
                String mDate = publishDate.format(new Date(timestamp));

                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        result.getTitle() + " - " +
                        result.getNewsSource() + " - " + mDate);
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        result.getUrl());
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        result.getSummary());
            } else {
                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        "Nothing found.");
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
