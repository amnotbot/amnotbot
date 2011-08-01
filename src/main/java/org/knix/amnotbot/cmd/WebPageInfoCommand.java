package org.knix.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.utils.Utf8ResourceBundle;
import org.knix.amnotbot.cmd.utils.WebPageInfoProxy;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        WebPageInfoProxy webPageInfo;
        webPageInfo = new WebPageInfoProxy(
                message.getText().trim().split("\\s+")[0]);
        String title = webPageInfo.getTitle();
        if (title != null) {
            message.getConn().doPrivmsg(message.getTarget(),
                    "[ " + title + " ]");
        }
    }

    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("WebPageInfoCommandBundle",
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
