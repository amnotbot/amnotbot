package org.knix.amnotbot.cmd.utils;

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

    @Override
    public String getUrl()
    {
        return this.url;
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
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
