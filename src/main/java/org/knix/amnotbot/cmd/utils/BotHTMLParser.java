package org.knix.amnotbot.cmd.utils;

import org.knix.amnotbot.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

public class BotHTMLParser
{

    private Parser parser;
    private String title = null;
    private String keywords = null;

    public BotHTMLParser(String url)
    {
        this.parser = new Parser();
        this.setUrl(url);
    }

    private boolean isValidURL(String url)
    {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            BotLogger.getDebugLogger().debug(e);
            return false;
        }

        URLConnection uc = null;
        try {
            uc = u.openConnection();
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug(e);
        }

        String type = uc.getContentType();
        if (type != null) {
            if (!type.startsWith("text")) {
                return false;
            }
        }

        return true;
    }

    public void setUrl(String url)
    {
        if (this.isValidURL(url)) {
            try {
                this.parser.setURL(url);
            } catch (ParserException pe) {
                BotLogger.getDebugLogger().debug(pe);
                return;
            }
            this.parse();
        }
    }

    private void setTitle(String title)
    {
        if (title != null) {
            if (title.trim().length() == 0) {
                title = null;
            }
        }
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    private void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public String getKeywords()
    {
        return this.keywords;
    }

    public String getUrl()
    {
        return this.parser.getURL();
    }

    private void parse()
    {
        this.parseHeaders();
        this.parseBody();
    }

    private void parseHeaders()
    {
        NodeList nl;

        try {
            nl = this.parser.parse(null);

            NodeClassFilter titleFilter = new NodeClassFilter(TitleTag.class);
            NodeList titles = nl.extractAllNodesThatMatch(titleFilter, true);
            if (titles.size() > 0) {
                TitleTag titletag = (TitleTag) titles.elementAt(0);
                this.setTitle(Translate.decode(titletag.getTitle().trim()));
            }

            NodeList meta = nl.extractAllNodesThatMatch(
                    new AndFilter(new NodeClassFilter(MetaTag.class),
                    new HasAttributeFilter("name", "keywords")), true);
            if (meta.size() > 0) {
                MetaTag metatag = (MetaTag) meta.elementAt(0);
                this.setKeywords(metatag.getMetaContent());
            }
        } catch (ParserException pe) {
            BotLogger.getDebugLogger().debug(pe);
            return;
        }
    }

    private void parseBody()
    {
    }
}
