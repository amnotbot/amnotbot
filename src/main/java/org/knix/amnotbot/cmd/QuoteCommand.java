package org.knix.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.Utf8ResourceBundle;
import org.knix.amnotbot.config.BotConfiguration;

public class QuoteCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        new QuoteImp(message).run();
    }

    @Override
    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("QuoteCommandBundle",
                currentLocale);

        Object[] messageArguments = {
            BotConfiguration.getConfig().getString("command_trigger"),
            BotConfiguration.getCommandsConfig().getString("QuoteCommand"),
            helpMessage.getString("short_description"),
            helpMessage.getString("options"),
            helpMessage.getString("set"),
            helpMessage.getString("text"),
            helpMessage.getString("text_example"),
            helpMessage.getString("delete"),
            helpMessage.getString("number"),
            helpMessage.getString("info"),
            helpMessage.getString("get"),
            helpMessage.getString("example")
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }
}
