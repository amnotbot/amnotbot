package com.github.amnotbot.cmd.utils;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoEntity implements WebPageInfo
{

    private String url;
    private String title;
    private String [] keywords;
    private String description;

    public WebPageInfoEntity()
    {
        this.url = null;
        this.title = null;
        this.description = null;
        this.keywords = new String [] {};
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String[] getKeywords()
    {
        return this.keywords;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setKeywords(String [] keywords)
    {
        this.keywords = keywords;
    }
}
