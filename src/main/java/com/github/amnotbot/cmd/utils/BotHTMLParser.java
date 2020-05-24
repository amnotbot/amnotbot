package com.github.amnotbot.cmd.utils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

import com.github.amnotbot.*;

public class BotHTMLParser implements WebPageInfo
{
    private Parser parser;
    private String title = null;
    private String description = null;
    private String[] keywords;
    private String url = null;
    private boolean parsed;

    public BotHTMLParser(String url)
    {
        this.url = url;
        this.parser = new Parser();
        this.keywords = new String[]{};
        this.parsed = false;
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getTitle()
    {
        if (!this.initialized()) {
            this.lazyParse();
        }
        return this.title;
    }

    public String getDescription()
    {
        if (!this.initialized()) {
            this.lazyParse();
        }
        return this.description;
    }

    public String[] getKeywords()
    {
        if (!this.initialized()) {
            this.lazyParse();
        }
        return this.keywords;
    }

    private boolean initialized()
    {
        boolean prev = this.parsed;
        this.parsed = true;
        return prev;
    }

    private void lazyParse()
    {
        try {
            this.parse(this.url);
        } catch (MalformedURLException e) {
            BotLogger.getDebugLogger().debug(e);
        } catch (ParserException e) {
            BotLogger.getDebugLogger().debug(e);
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug(e);
        }
    }

    private boolean isTextContent(String url)
            throws MalformedURLException, IOException
    {
        URL u = new URL(url);
        URLConnection uc = u.openConnection();

        String type = uc.getContentType();
        if (type != null) {
            if (!type.startsWith("text")) {
                return false;
            }
        }
        return true;
    }

    private void setTitle(String title)
    {
        title.trim().replaceAll("\n", "");
        this.title = title;
    }

    private void setDescription(String description)
    {
        this.description = description.trim().replaceAll("\n", "");
    }

    private void setKeywords(String keywords)
    {
        String[] str;

        str = keywords.split(",");
        if (str.length == 1) {
            str = keywords.split(" ");
        }

        for (int i = 0; i < str.length; ++i) {
            str[i] = str[i].trim();
        }

        this.keywords = str.clone();
    }

    private void parse(String url)
            throws MalformedURLException, ParserException, ParserException,
            IOException
    {
        if (this.isTextContent(url)) {
            this.parser.setURL(url);
            this.parseHeaders();
            this.parseBody();
        }
    }

    private void parseHeaders() throws ParserException, UnsupportedEncodingException {
        NodeList nl;

        nl = this.parser.parse(null);

        NodeClassFilter titleFilter = new NodeClassFilter(TitleTag.class);
        NodeList titles = nl.extractAllNodesThatMatch(titleFilter, true);
        if (titles.size() > 0) {
            TitleTag titletag = (TitleTag) titles.elementAt(0);
            this.setTitle(Translate.decode(
                    new String(titletag.getTitle().trim().getBytes(this.parser.getEncoding()))));
        }

        NodeList keywordsList = nl.extractAllNodesThatMatch(
                new AndFilter(new NodeClassFilter(MetaTag.class),
                new HasAttributeFilter("name", "keywords")), true);
        if (keywordsList.size() > 0) {
            MetaTag metatag = (MetaTag) keywordsList.elementAt(0);
            this.setKeywords(metatag.getMetaContent());
        }

        NodeList descriptions = nl.extractAllNodesThatMatch(
                new AndFilter(new NodeClassFilter(MetaTag.class),
                new HasAttributeFilter("name", "description")), true);
        if (descriptions.size() > 0) {
            MetaTag metatag = (MetaTag) descriptions.elementAt(0);
            this.setDescription(Translate.decode(metatag.getMetaContent()));
        }
    }

    private void parseBody()
    {
    }
}
