package org.knix.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.Utf8ResourceBundle;
import org.knix.amnotbot.config.BotConfiguration;

public class DeliciousCommand implements BotCommand
{
    private int maxTagLength;
    private DeliciousBookmarks delicious;

    public DeliciousCommand()
    {      
        this.delicious = new DeliciousBookmarks();
        Configuration config = BotConfiguration.getConfig();
        this.maxTagLength =
                config.getInteger("delicious_max_tag_length", 30).intValue();
    }

    @Override
    public void execute(BotMessage message)
    {
        DeliciousImp del = new DeliciousImp(this.delicious, message,
                this.maxTagLength);

        del.run();
    }

    @Override
    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("DeliciousCommandBundle",
                currentLocale);

        Object[] messageArguments = {
            helpMessage.getString("short_description"),
            helpMessage.getString("example"),
            helpMessage.getString("title"),
            helpMessage.getString("title_example"),
            helpMessage.getString("tags"),
            helpMessage.getString("tags_example"),
            helpMessage.getString("comment"),
            helpMessage.getString("comment_example"),
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }
}
