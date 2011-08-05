package com.github.amnotbot.cmd.utils;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoProxy implements WebPageInfo
{
    String url;
    WebPageInfo info, parser;

    public WebPageInfoProxy(String url)
    {
        this.url = url;
        this.info = null;
        this.parser = new BotHTMLParser(url);
    }

    private WebPageInfo getWebPageInfo()
    {
        WebPageInfo _info = null;

        _info = WebPageCache.instance().get(this.url);
        if (_info != null) return _info;

        WebPageInfoEntity entity = new WebPageInfoEntity();
        entity.setUrl(this.parser.getUrl());
        entity.setTitle(this.parser.getTitle());
        entity.setDescription(this.parser.getDescription());
        entity.setKeywords(this.parser.getKeywords());
        
        if (entity.getTitle() != null || entity.getDescription() != null
                || entity.getKeywords().length != 0) {
            WebPageCache.instance().put(entity);
            return entity;
        }
        return new WebPageInfoEntity();
    }

    public String getUrl()
    {
        if (this.info != null) {
            return this.info.getUrl();
        }

        this.info = this.getWebPageInfo();

        return this.info.getUrl();
    }

    public String getTitle()
    {
        if (this.info != null) {
            return this.info.getTitle();
        }

        this.info = this.getWebPageInfo();

        return this.info.getTitle();
    }

    public String getDescription()
    {
        if (this.info != null) {
            return this.info.getDescription();
        }

        this.info = this.getWebPageInfo();

        return this.info.getDescription();
    }

    public String[] getKeywords()
    {
        if (this.info != null) {
            return this.info.getKeywords();
        }
        
        this.info = this.getWebPageInfo();
        
        return this.info.getKeywords();
    }
}
