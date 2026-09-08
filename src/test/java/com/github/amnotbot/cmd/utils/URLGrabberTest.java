package com.github.amnotbot.cmd.utils;

import com.github.amnotbot.BotCommandEvent;
import java.io.File;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;

public class URLGrabberTest
{
    @Test
    public void capturesModernDomainsAndCompleteUrls() throws Exception
    {
        PropertiesConfiguration config = new PropertiesConfiguration(
                new File("src/main/resources/commands.config"));
        BotCommandEvent trigger = new BotCommandEvent(
                config.getString("BookmarkCommand"));
        String[] urls = {
            "https://my-site.technology/path?q=one&b=two#section",
            "https://sub-domain.example.dev:8443/path",
            "HTTPS://EXAMPLE.MUSEUM/path",
            "https://example.xn--p1ai/path",
            "https://example.dev/search?q=\"hello\"&path=\\docs"
        };
        for (String url : urls) {
            String text = "look at <" + url + "> please";
            URLGrabber grabber = new URLGrabber(text);
            assertTrue(trigger.test(text));
            assertTrue(grabber.hasURL());
            assertEquals(url, grabber.getURL());
            assertTrue(grabber.hasURL());
            assertEquals(url, grabber.getURL());
        }
    }

    @Test
    public void returnsFirstUrl()
    {
        assertEquals("https://first.dev/a", new URLGrabber(
                "https://first.dev/a and https://second.technology/b").getURL());
    }

    @Test
    public void ignoresTextWithoutHttpUrl()
    {
        for (String text : new String[] {"", "hello", "example.dev", "https://"}) {
            URLGrabber grabber = new URLGrabber(text);
            assertFalse(grabber.hasURL());
            assertEquals("", grabber.getURL());
        }
    }
}
