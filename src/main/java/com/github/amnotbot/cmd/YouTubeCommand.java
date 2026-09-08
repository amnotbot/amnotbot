package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.config.BotConfiguration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Searches YouTube for the first video ranked by relevance. */
public class YouTubeCommand implements BotCommand
{
    private static final Pattern TRIGGER =
            Pattern.compile("(?i)^!yt(?:\\s+(.*))?$");

    /** Replies to the channel with one video or a concise error. */
    @Override
    public void execute(BotMessage message)
    {
        Matcher matcher = TRIGGER.matcher(message.getText().trim());
        if (!matcher.matches()) {
            return;
        }
        String query = matcher.group(1);
        if (query == null || query.trim().isEmpty()) {
            message.getConn().doPrivmsg(message.getTarget(), help());
            return;
        }
        String key = BotConfiguration.getConfig()
                .getString("youtube_api_key", "");
        String reply = new YouTubeSearch(key).search(query.trim());
        message.getConn().doPrivmsg(message.getTarget(), reply);
    }

    /** Describes the command syntax. */
    @Override
    public String help()
    {
        return "Usage: !yt <search terms> - top YouTube video with details";
    }
}
