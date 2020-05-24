package com.github.amnotbot.cmd;

import com.github.amnotbot.config.BotConfiguration;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import com.github.amnotbot.BotMessage;

import org.apache.hc.core5.http.ParseException;
import org.schwering.irc.lib.IRCConstants;

import java.io.IOException;

/**
 * Created by Geronimo on 21/11/16.
 */
public class SpotifyImp {

    private final BotMessage msg;

    public enum searchType {
        ARTIST_SEARCH, ALBUM_SEARCH, TRACK_SEARCH
    }

    private static final String clientId = BotConfiguration.getConfig().getString("spotify_client_id");
    private static final String clientSecret = BotConfiguration.getConfig().getString("spotify_client_secret");
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

    public SpotifyImp(BotMessage msg) {
        this.msg = msg;

        this.obtainAccessToken();
    }

    public void obtainAccessToken() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn() + " Access Token is: "
                    + clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void search(searchType sType, String id) {
        switch (sType) {
            case ARTIST_SEARCH:
                final GetArtistRequest artistRequest = spotifyApi.getArtist(id).build();

                try {
                    final Artist artist = artistRequest.execute();

                    msg.getConn().doPrivmsg(msg.getTarget(),
                            "[ " + IRCConstants.UNDERLINE_INDICATOR + "Artist: " + IRCConstants.UNDERLINE_INDICATOR
                                    + artist.getName() + IRCConstants.UNDERLINE_INDICATOR + "  Ranking: "
                                    + IRCConstants.UNDERLINE_INDICATOR + artist.getPopularity() + " ]");

                } catch (Exception e) {
                    msg.getConn().doPrivmsg(msg.getTarget(), "No artist found!");
                }
                break;
            case ALBUM_SEARCH:
                GetAlbumRequest albumRequest = spotifyApi.getAlbum(id).build();

                try {
                    Album album = albumRequest.execute();

                    String mArtists = new String();
                    ArtistSimplified[] artists = album.getArtists();
                    for (ArtistSimplified artist : artists) {
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
                GetTrackRequest trackRequest = spotifyApi.getTrack(id).build();

                try {
                    final Track track = trackRequest.execute();

                    String mArtists = new String();
                    ArtistSimplified [] artists = track.getArtists();
                    boolean isFirst = true;
                    for (ArtistSimplified artist : artists) {
                        if (isFirst) {
                            mArtists += artist.getName();
                            isFirst = false;
                        } else {
                            mArtists += ", " + artist.getName();
                        }
                    }

                    int minutes = track.getDurationMs() / 1000 / 60;
                    int seconds = track.getDurationMs() / 1000 - (minutes * 60);

                    Integer secs = Integer.valueOf(seconds);
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
