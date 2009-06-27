package org.knix.amnotbot.cmd.utils;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoProxy implements WebPageInfo
{
    String url;
    WebPageInfo info;
    BotHTMLParser parser;

    public WebPageInfoProxy(String url)
    {
        this.url = url;
        this.info = null;
        this.parser = new BotHTMLParser();
    }

    private WebPageInfo getWebPageInfo()
    {
        WebPageInfo _info = null;

        _info = WebPageCache.instance().get(this.url);
        if (_info != null) return _info;

        _info = this.parser.get(this.url);
        if (_info != null) {
            WebPageCache.instance().put(_info);
            return _info;
        }
        return new WebPageInfoEntity();
    }

    @Override
    public String getUrl()
    {
        if (this.info != null) {
            return this.info.getUrl();
        }

        this.info = this.getWebPageInfo();

        return this.info.getUrl();
    }

    @Override
    public String getTitle()
    {
        if (this.info != null) {
            return this.info.getTitle();
        }

        this.info = this.getWebPageInfo();

        return this.info.getTitle();
    }

    @Override
    public String getDescription()
    {
        if (this.info != null) {
            return this.info.getDescription();
        }

        this.info = this.getWebPageInfo();

        return this.info.getDescription();
    }

    @Override
    public String[] getKeywords()
    {
        if (this.info != null) {
            return this.info.getKeywords();
        }
        
        this.info = this.getWebPageInfo();
        
        return this.info.getKeywords();
    }
}
