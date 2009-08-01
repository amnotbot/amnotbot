package org.knix.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.Utf8ResourceBundle;
import org.knix.amnotbot.config.BotConfiguration;

public class LinesCommand implements BotCommand
{

    public LinesCommand()
    {
    }

    @Override
    public void execute(BotMessage message)
    {
        new WordsCommandImp(message,
                WordsCommandImp.countOperation.LINES).run();
    }

    @Override
    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("LinesCommandBundle",
                currentLocale);

        Object[] messageArguments = {
            BotConfiguration.getConfig().getString("command_trigger"),
            BotConfiguration.getCommandsConfig().getString("LinesCommand"),
            helpMessage.getString("short_description"),
            helpMessage.getString("options"),
            helpMessage.getString("average"),
            helpMessage.getString("nick"),
            helpMessage.getString("nick_example"),
            helpMessage.getString("date"),
            helpMessage.getString("number"),
            helpMessage.getString("date_description"),
            helpMessage.getString("example")
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }
}
