package com.github.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.URLGrabber;
import com.github.amnotbot.cmd.utils.Utf8ResourceBundle;
import com.github.amnotbot.config.BotConfiguration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QurlRequestCommand implements BotCommand
{
    int qurl_length;

    public QurlRequestCommand()
    {       
        Configuration config;
        config = BotConfiguration.getConfig();
        this.qurl_length = config.getInt("qurl_length", 83);
    }

    public void execute(BotMessage message)
    {
        URLGrabber urlGrabber = new URLGrabber(message.getText());
        String url = urlGrabber.getURL();
        System.out.println("URL: " + url + " length " + url.length());
        if (url.length() > this.qurl_length) {
            new QurlRequest(message, url).run();
        }
    }

    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("QurlRequestCommandBundle",
                currentLocale);

        Object[] messageArguments = {
            helpMessage.getString("short_description"),
            helpMessage.getString("long_description"),
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }
}
