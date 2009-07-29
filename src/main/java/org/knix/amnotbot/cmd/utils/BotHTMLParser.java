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
    private String description = null;
    private String [] keywords;

    public BotHTMLParser()
    {       
        this.parser = new Parser();
        this.keywords = new String [] {};
    }
    
    public WebPageInfo get(String url)
    {
        WebPageInfo webPageInfo = null;

        try {
            webPageInfo = this.getWebPageInfo(url);
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            return null;
        }
        
        return webPageInfo;
    }

    private WebPageInfo getWebPageInfo(String url)
            throws ParserException, MalformedURLException, IOException
    {
        WebPageInfoEntity webPageInfo = new WebPageInfoEntity();

        if (this.isTextContent(url)) {            
            this.parse(url);
            
            webPageInfo.setUrl(url);
            webPageInfo.setTitle(this.getTitle());
            webPageInfo.setDescription(this.getDescription());
            webPageInfo.setKeywords(this.getKeywords());
        }
        return webPageInfo;
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
        if (title != null) {
            title = title.trim();
            if (title.length() == 0) {
                title = null;
            } else {
                String [] str;
                String tmp = new String();
                str = title.split("\n");
                for (String t : str) {
                    tmp += " " + t.trim();
                }
                title = tmp.trim();
            }
        }
        this.title = title;
    }

    private String getTitle()
    {
        return this.title;
    }

    private void setDescription(String description)
    {
        this.description = description;
    }

    private String getDescription()
    {
        return this.description;
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

    private String [] getKeywords()
    {       
        return this.keywords;
    }

    private void parse(String url) throws ParserException
    {
        this.parser.setURL(url);
        
        this.parseHeaders();
        this.parseBody();
    }

    private void parseHeaders() throws ParserException
    {
        NodeList nl;
       
        nl = this.parser.parse(null);

        NodeClassFilter titleFilter = new NodeClassFilter(TitleTag.class);
        NodeList titles = nl.extractAllNodesThatMatch(titleFilter, true);
        if (titles.size() > 0) {
            TitleTag titletag = (TitleTag) titles.elementAt(0);
            this.setTitle(Translate.decode(titletag.getTitle().trim()));
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
