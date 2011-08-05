package com.github.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.Utf8ResourceBundle;
import com.github.amnotbot.config.BotConfiguration;

public class YahooNewsSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new YahooImp(message, YahooImp.searchType.NEWS_SEARCH).run();
    }

    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("YahooSearchCommandBundle",
                currentLocale);

        Configuration cmdConfig = BotConfiguration.getCommandsConfig();
        String cmd = cmdConfig.getString("YahooNewsSearchCommand");

        Object[] messageArguments = {
            BotConfiguration.getConfig().getString("command_trigger"),
            cmd,
            helpMessage.getString("news_short_description"),
            helpMessage.getString("search_term"),
            helpMessage.getString("region"),
            helpMessage.getString("format"),
            helpMessage.getString("language"),
            helpMessage.getString("country"),
            helpMessage.getString("site"),
            helpMessage.getString("adult_ok"),
            helpMessage.getString("license"),
            helpMessage.getString("type"),
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }
}
