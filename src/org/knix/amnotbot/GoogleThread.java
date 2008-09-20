package org.knix.amnotbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.Service;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.extensions.Comments;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaPlayer;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.util.ServiceException;

/**
 * Created by IntelliJ IDEA.
 * User: gpoppino
 * Date: Oct 27, 2007
 * Time: 3:35:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleThread extends Thread {
		
	private BotConnection con;
	private String query;
	private String chan;
	private String nick;

	private YouTubeService youTube;

	/**
	 * The name of the server hosting the YouTube GDATA feeds
	 */
	public static final String YOUTUBE_GDATA_SERVER = "http://gdata.youtube.com";


	/**
	 * The URL of the "Videos" feed
	 */
	public static final String VIDEOS_FEED = YOUTUBE_GDATA_SERVER
		+ "/feeds/videos";


	public GoogleThread(BotConnection con,
			String chan,
			String nick,
			String query)
	{
		this.con = con;
		this.chan = chan;
		this.nick = nick;
		this.query = this.getVideoID(query);
	
		this.youTube = new YouTubeService("Amnotbot");

		start();
	}

	public void run()
	{
		try {
			this.searchVideoEntry();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();			
		}
	}

	private String getVideoID(String query)
	{
		String videoID;

		videoID = query.substring(query.indexOf("v=") + 2, query.length());
		BotLogger.getDebugLogger().debug("video ID: " + videoID);

		return videoID;
	}

	private void searchVideoEntry() throws IOException, ServiceException
	{
		VideoEntry ve = this.youTube.getEntry(new URL(VIDEOS_FEED + "/" + this.query), VideoEntry.class);

		this.printVideoEntry("", ve, false);
	}	

	/**
	 * Prints a VideoEntry, optionally showing its responses and comment feeds.
	 *
	 * @param prefix                   a string to be shown before each entry
	 * @param videoEntry               the VideoEntry to be printed
	 * @param showCommentsAndResponses true if the comments and responses feeds
	 *                                 should be printed
	 * @throws ServiceException
	 *                                 If the service is unable to handle the
	 *                                 request.
	 * @throws IOException             error sending request or reading the feed.
	 */
	private void printVideoEntry(String prefix, VideoEntry videoEntry,
					    boolean showCommentsAndResponses) throws IOException, ServiceException {

		this.con.doPrivmsg(this.chan, prefix);
		if (videoEntry.getTitle() != null) {
			this.con.doPrivmsg(this.chan, "Title: " +  videoEntry.getTitle().getPlainText());
		}
		if (videoEntry.getSummary() != null) {
			BotLogger.getDebugLogger().debug("Summary: " + videoEntry.getSummary().getPlainText());
		}
		YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
		if (mediaGroup != null) {
			MediaPlayer mediaPlayer = mediaGroup.getPlayer();
			BotLogger.getDebugLogger().debug("Web Player URL: " + mediaPlayer.getUrl());
			MediaKeywords keywords = mediaGroup.getKeywords();
			System.out.print("Keywords: ");
			for(String keyword : keywords.getKeywords()) {
				System.out.print(keyword + ",");
			}
			BotLogger.getDebugLogger().debug("\tThumbnails:");
			for(MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
				BotLogger.getDebugLogger().debug("\t\tThumbnail URL: " + mediaThumbnail.getUrl());
				BotLogger.getDebugLogger().debug("\t\tThumbnail Time Index: " +
					mediaThumbnail.getTime());
			}
			BotLogger.getDebugLogger().debug("\tMedia:");
			for (YouTubeMediaContent mediaContent : mediaGroup.getYouTubeContents()) {
				BotLogger.getDebugLogger().debug("\t\tMedia Location: "+mediaContent.getUrl());
				BotLogger.getDebugLogger().debug("\t\tMedia Type: "+mediaContent.getType());

				int minutes = mediaContent.getDuration() / 60;
				int seconds = mediaContent.getDuration() % 60;
				String mDuration = minutes + ":" + seconds;
				if (minutes == 0) {
					mDuration += " s";
				} else {
					mDuration += " m";
				}
				
				//this.con.doPrivmsg(this.chan, "Duration: " + mDuration);
				BotLogger.getDebugLogger().debug("Duration: " + mDuration);
			}
		}
		if (showCommentsAndResponses) {
			this.printResponsesFeed(videoEntry);
			BotLogger.getDebugLogger().debug("");
			this.printCommentsFeed(videoEntry);
			BotLogger.getDebugLogger().debug("");
			BotLogger.getDebugLogger().debug("");
		}
	}

	/**
	 * Prints the responses feed of a VideoEntry.
	 *
	 * @param videoEntry the VideoEntry whose responses are to be printed
	 * @throws ServiceException
	 *                     If the service is unable to handle the request.
	 * @throws IOException error sending request or reading the feed.
	 */
	private void printResponsesFeed(VideoEntry videoEntry)
		throws IOException, ServiceException {
		if (videoEntry.getVideoResponsesLink() != null) {
			String videoResponsesFeedUrl =
				videoEntry.getVideoResponsesLink().getHref();
			this.printVideoFeed((YouTubeService) videoEntry.getService(),
				videoResponsesFeedUrl, false);
		}
	}

	/**
	 * Prints the comments feed of a VideoEntry.
	 *
	 * @param videoEntry the VideoEntry whose comments are to be printed
	 * @throws ServiceException
	 *                     If the service is unable to handle the request.
	 * @throws IOException error sending request or reading the feed.
	 */
	private void printCommentsFeed(VideoEntry videoEntry)
		throws IOException, ServiceException {
		Comments comments = videoEntry.getComments();
		if (comments != null && comments.getFeedLink() != null) {
			BotLogger.getDebugLogger().debug("\tComments:");
			this.printFeed(videoEntry.getService(), comments.getFeedLink().getHref(),
				"Comment");
		}
	}

	/**
	 * Prints a basic feed, such as the comments or responses feed, retrieved from
	 * a feedUrl.
	 *
	 * @param service a YouTubeService object
	 * @param feedUrl the url of the feed
	 * @param prefix  a prefix string to be printed in front of each entry field
	 * @throws ServiceException
	 *                     If the service is unable to handle the request.
	 * @throws IOException error sending request or reading the feed.
	 */
	private void printFeed(Service service, String feedUrl, String prefix)
		throws IOException, ServiceException {
		Feed feed = service.getFeed(new URL(feedUrl), Feed.class);

		for (Entry e : feed.getEntries()) {
			this.printEntry(e, prefix);
		}
	}

	/**
	 * Prints a basic Entry, usually from a comments or responses feed.
	 *
	 * @param entry      the entry to be printed
	 * @param prefix a prefix to be printed before each entry attribute
	 */
	private void printEntry(Entry entry, String prefix) {
		BotLogger.getDebugLogger().debug("\t\t" + prefix + " Title: "
			+ entry.getTitle().getPlainText());
		if (entry.getContent() != null) {
			TextContent content = (TextContent) entry.getContent();
			BotLogger.getDebugLogger().debug("\t\t" + prefix + " Content: "
				+ content.getContent().getPlainText());
		}
		BotLogger.getDebugLogger().debug("\t\tURL: " + entry.getHtmlLink().getHref());
	}

	/**
	 * Fetches a feed known to be a VideoFeed, printing each VideoEntry with in
	 * a numbered list, optionally prompting the user for the number of a video
	 * entry which should have its comments and responses printed.
	 *
	 * @param service a YouTubeService object
	 * @param feedUrl the url of the video feed to print
	 * @param showCommentsAndResponses true if the user should be prompted for
	 *                                 a video whose comments and responses should
	 *                                 printed
	 * @throws ServiceException
	 *                     If the service is unable to handle the request.
	 * @throws IOException error sending request or reading the feed.
	 */
	private void printVideoFeed(YouTubeService service, String feedUrl,
					   boolean showCommentsAndResponses) throws IOException, ServiceException {
		VideoFeed videoFeed = service.getFeed(new URL(feedUrl), VideoFeed.class);
		String title = videoFeed.getTitle().getPlainText();
		if (showCommentsAndResponses) {
			title += " with comments and responses";
		}
//		printUnderlined(title);
		List<VideoEntry> videoEntries = videoFeed.getEntries();
		if (videoEntries.size() == 0) {
			BotLogger.getDebugLogger().debug("This feed contains no entries.");
			return;
		}
		int count = 1;
		for (VideoEntry ve : videoEntries) {
			printVideoEntry("(Video #" + String.valueOf(count) + ")", ve, false);
			count++;
		}

		if (showCommentsAndResponses) {
			System.out.printf(
				"\nWhich video to show comments and responses for? (1-%d): \n",
				count - 1);
			int whichVideo = readInt();
			this.printVideoEntry("", videoEntries.get(whichVideo - 1), true);
		}
	}

	/**
	 * Reads a line of text from the standard input, and returns the parsed
	 * integer representation.
	 * @throws IOException if unable to read a line from the standard input
	 * @return an integer read from the standard input
	 */
	private int readInt() throws IOException {
		String input = readLine();
		return Integer.parseInt(input);
	}

	/**
	 * Reads a line of text from the standard input.
	 * @throws IOException if unable to read a line from the standard input
	 * @return a line of text read from the standard input
	 */
	private String readLine() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(System.in));
		return bufferedReader.readLine();
	}

}
