package com.github.amnotbot.cmd.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gpoppino
 */
public class URLGrabber 
{
    
    private Matcher m;
    
    public URLGrabber(String text)
    {
        Pattern urlPattern = Pattern.compile(
                ".*((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)");
        this.m = urlPattern.matcher(text);
    }
    
    public boolean hasURL()
    {
        return this.m.find();
    }
    
    public String getURL()
    {
        String url = "";
        if (this.hasURL()) {
            url = this.m.group(1).trim().split("\\s+")[0];
        }
        return url;
    }
}
