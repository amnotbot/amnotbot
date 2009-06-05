package org.knix.amnotbot.cmd;

import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.*;
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

    public void execute(BotMessage message)
    {
        String url = message.getText().trim().split("\\s+")[0];
        if (url.length() > this.qurl_length) {
            new QurlRequest(message);
        }
    }

    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
