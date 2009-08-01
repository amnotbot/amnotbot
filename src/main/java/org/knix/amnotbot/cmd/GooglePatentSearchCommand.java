package org.knix.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.Utf8ResourceBundle;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class GooglePatentSearchCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        new GoogleSearchImp(
                GoogleSearch.searchType.PATENT_SEARCH,
                new GoogleResultOutputPatentStrategy(),
                message
                ).run();
    }

    @Override
    public String help()
    {
        String msg = new String();
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle(
                "GoogleBundle", currentLocale);

        Configuration cmdConfig = BotConfiguration.getCommandsConfig();
        String cmd = cmdConfig.getString("GooglePatentSearchCommand");

        Object[] messageArguments = {
            BotConfiguration.getConfig().getString("command_trigger"),
            cmd,
            helpMessage.getString("patent_short_description"),
            helpMessage.getString("parameters"),
            helpMessage.getString("search_term"),
            helpMessage.getString("example")
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }

}
