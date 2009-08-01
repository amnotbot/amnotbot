package org.knix.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.Utf8ResourceBundle;
import org.knix.amnotbot.config.BotConfiguration;

public class QurlRequestCommand implements BotCommand
{
    int qurl_length;

    public QurlRequestCommand()
    {       
        Configuration config;
        config = BotConfiguration.getConfig();
        this.qurl_length = config.getInt("qurl_length", 83);
    }

    @Override
    public void execute(BotMessage message)
    {
        String url = message.getText().trim().split("\\s+")[0];
        if (url.length() > this.qurl_length) {
            new QurlRequest(message).run();
        }
    }

    @Override
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
