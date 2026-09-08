package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.cmd.utils.URLGrabber;

import org.apache.commons.configuration.Configuration;

public class BookmarkCommand implements BotCommand
{
    @Override
    public void execute(BotMessage message)
    {
        URLGrabber grabber = new URLGrabber(message.getText());
        if (!grabber.hasURL()) return;

        String url = grabber.getURL();
        Configuration config = BotConfiguration.getConfig();
        String backendUrl = config.getString("bookmark_backend_url");
        String jwtToken = config.getString("bookmark_jwt_token");

        BookmarkImp imp = new BookmarkImp(backendUrl, jwtToken, url);
        imp.run(message);
    }

    @Override
    public String help()
    {
        return "Automatically saves URLs posted in the channel to the bookmark backend.";
    }
}
