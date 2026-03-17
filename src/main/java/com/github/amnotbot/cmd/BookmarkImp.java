package com.github.amnotbot.cmd;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.BotURLConnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class BookmarkImp
{
    private final String backendUrl;
    private final String jwtToken;
    private final String url;

    public BookmarkImp(String backendUrl, String jwtToken, String url)
    {
        this.backendUrl = backendUrl;
        this.jwtToken = jwtToken;
        this.url = url;
    }

    /**
     * Posts the URL to the bookmark backend and notifies the channel on success.
     *
     * @param message the IRC message that triggered this command
     */
    public void run(BotMessage message)
    {
        BotURLConnection conn;
        try {
            conn = new BotURLConnection(new URL(this.backendUrl + "/api/bookmarks"));
        } catch (MalformedURLException e) {
            BotLogger.getDebugLogger().debug(e);
            return;
        }

        conn.addHeader("Content-Type", "application/json");
        conn.addHeader("Cookie", "access_token=" + this.jwtToken);

        String payload = "{\"url\": \"" + this.url + "\"}";
        try {
            conn.postToURL(payload);
            message.getConn().doPrivmsg(message.getTarget(), "Bookmarked: " + this.url);
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug(e);
        }
    }
}
