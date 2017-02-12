package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;

/**
 * Created by Geronimo on 21/11/16.
 */
public class SpotifyCommand implements BotCommand
{
    @Override
    public void execute(BotMessage message)
    {
        final String[] tmp = message.getText().split(":");
        final String type = tmp[1];
        final String id = tmp[2];

        SpotifyImp spotify = new SpotifyImp(message);

        if (type.compareTo("artist") == 0) {
            spotify.search(SpotifyImp.searchType.ARTIST_SEARCH, id);
        } else if (type.compareTo("album") == 0) {
            spotify.search(SpotifyImp.searchType.ALBUM_SEARCH, id);
        } else if (type.compareTo("track") == 0) {
            spotify.search(SpotifyImp.searchType.TRACK_SEARCH, id);
        } else {
            message.getConn().doPrivmsg(message.getTarget(), "Valid types: artist, album or track (second field)");
        }
    }

    @Override
    public String help()
    {
        return null;
    }
}
