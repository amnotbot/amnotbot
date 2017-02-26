package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.ArtistRequest;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import org.schwering.irc.lib.IRCConstants;

import java.util.List;

/**
 * Created by Geronimo on 21/11/16.
 */
public class SpotifyImp
{

    private final BotMessage msg;

    public enum searchType {
        ARTIST_SEARCH, ALBUM_SEARCH, TRACK_SEARCH
    }

    private searchType sType;

    public SpotifyImp(BotMessage msg)
    {
        this.msg = msg;
    }

    public void search(searchType sType, String id)
    {
        Api api = Api.DEFAULT_API;

        switch (sType) {
            case ARTIST_SEARCH:
                final ArtistRequest artistRequest = api.getArtist(id).build();

                try {
                    final Artist artist = artistRequest.get();

                    msg.getConn().doPrivmsg(msg.getTarget(),
                            "[ " + IRCConstants.UNDERLINE_INDICATOR + "Artist: " + IRCConstants.UNDERLINE_INDICATOR +
                                    artist.getName() + IRCConstants.UNDERLINE_INDICATOR + "  Ranking: " +
                                    IRCConstants.UNDERLINE_INDICATOR + artist.getPopularity() + " ]");

                } catch (Exception e) {
                    msg.getConn().doPrivmsg(msg.getTarget(), "No artist found!");
                }
                break;
            case ALBUM_SEARCH:
                AlbumRequest albumRequest = api.getAlbum(id).build();

                try {
                    Album album = albumRequest.get();

                    String mArtists = new String();
                    List<SimpleArtist> artists = album.getArtists();
                    for (SimpleArtist artist : artists) {
                        mArtists += " " + artist.getName();
                    }
                    msg.getConn().doPrivmsg(msg.getTarget(),
                            "[ " + IRCConstants.UNDERLINE_INDICATOR + " Album: " + IRCConstants.UNDERLINE_INDICATOR +
                                    album.getName() + IRCConstants.UNDERLINE_INDICATOR + "  Artists: " +
                                    IRCConstants.UNDERLINE_INDICATOR + mArtists + IRCConstants.UNDERLINE_INDICATOR +
                                    "  Release date: " + IRCConstants.UNDERLINE_INDICATOR + album.getReleaseDate() +
                                    IRCConstants.UNDERLINE_INDICATOR + "  Songs: " + IRCConstants.UNDERLINE_INDICATOR +
                                    album.getTracks().getTotal() + " ]");
                } catch (Exception e) {
                    msg.getConn().doPrivmsg(msg.getTarget(), "Could not get album!");
                }
                break;
            case TRACK_SEARCH:
                TrackRequest trackRequest = api.getTrack(id).build();

                try {
                    final Track track = trackRequest.get();

                    String mArtists = new String();
                    List<SimpleArtist> artists = track.getArtists();
                    boolean isFirst = true;
                    for (SimpleArtist artist : artists) {
                        if (isFirst) {
                            mArtists += artist.getName();
                            isFirst = false;
                        } else {
                            mArtists += ", " + artist.getName();
                        }
                    }

                    int minutes = track.getDuration() / 1000 / 60;
                    int seconds = track.getDuration() / 1000 - (minutes * 60);

                    Integer secs = new Integer(seconds);
                    String secondsString = seconds < 10 ? "0" + secs.toString() : secs.toString();

                    msg.getConn().doPrivmsg(msg.getTarget(),
                            "[ " + IRCConstants.UNDERLINE_INDICATOR + " Track: "  + IRCConstants.UNDERLINE_INDICATOR +
                                    track.getName() + IRCConstants.UNDERLINE_INDICATOR + "  Artists: " +
                                    IRCConstants.UNDERLINE_INDICATOR + mArtists + IRCConstants.UNDERLINE_INDICATOR +
                                    "  Duration: " + IRCConstants.UNDERLINE_INDICATOR + minutes + ":" +
                                    secondsString + IRCConstants.UNDERLINE_INDICATOR + " Preview: " +
                                    IRCConstants.UNDERLINE_INDICATOR + track.getPreviewUrl() + " ]");
                } catch (Exception e) {
                    msg.getConn().doPrivmsg(msg.getTarget(), "Track not found!");
                }
                break;
        }
    }

}
