package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.Utf8ResourceBundle;
import com.github.amnotbot.config.BotConfiguration;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author gpoppino
 */
public class UniqueWordsCommand implements BotCommand 
{

    @Override
    public void execute(BotMessage message) 
    {
       new WordsCommandImp(message,
                WordsCommandImp.countOperation.UNIQUEWORDS).run();
    }

    @Override
    public String help() 
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("UniqueWordsCommandBundle",
                currentLocale);

        Object[] messageArguments = {
            BotConfiguration.getConfig().getString("command_trigger"),
            BotConfiguration.getCommandsConfig().getString("UniqueWordsCommand"),
            helpMessage.getString("short_description"),
            helpMessage.getString("options"),
            helpMessage.getString("number"),
            helpMessage.getString("date"),
            helpMessage.getString("nick"),
            helpMessage.getString("nick_example"),
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
