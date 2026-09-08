package com.github.amnotbot.cmd;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Exercises API requests and IRC output against a local HTTP server. */
public class YouTubeSearchTest
{
    private HttpServer server;
    private YouTubeSearch client;
    private JSONObject video;
    private String searchBody;
    private String detailsBody;
    private int status;
    private final List<String> requests = new ArrayList<>();

    /** Starts the fake YouTube API. */
    @Before
    public void setUp() throws Exception
    {
        status = 200;
        searchBody = "{\"items\":[{\"id\":{\"videoId\":\"abcdefghijk\"}}]}";
        video = new JSONObject()
                .put("snippet", new JSONObject().put("title", "Guitar Hero")
                        .put("publishedAt", "2020-02-03T12:34:56Z"))
                .put("contentDetails", new JSONObject()
                        .put("duration", "PT4M9S"))
                .put("statistics", new JSONObject().put("viewCount", "12345")
                        .put("likeCount", "678"));
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.add(exchange.getRequestURI().toString());
            String body = exchange.getRequestURI().getPath().equals("/search")
                    ? searchBody : detailsBody == null
                    ? new JSONObject().put("items", new JSONArray()
                            .put(video)).toString() : detailsBody;
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        client = new YouTubeSearch("test-key", endpoint());
    }

    private String endpoint()
    {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    /** Stops the fake API. */
    @After
    public void tearDown()
    {
        server.stop(0);
    }

    /** Searches for one video and fetches all metadata. */
    @Test
    public void returnsFirstResultAndEncodesQuery()
    {
        String query = "guitár & hero+ #1";
        assertEquals("https://www.youtube.com/watch?v=abcdefghijk"
                + " | Guitar Hero | Duration: 4:09 | Published: 2020-02-03"
                + " | Views: 12345 | Likes: 678", client.search(query));
        assertEquals(2, requests.size());
        assertTrue(requests.get(0).contains("type=video"));
        assertTrue(requests.get(0).contains("order=relevance&maxResults=1"));
        assertTrue(requests.get(0).contains("q=guit%C3%A1r+%26+hero%2B+%231"));
        assertTrue(URLDecoder.decode(requests.get(0), StandardCharsets.UTF_8)
                .contains("q=" + query + "&key=test-key"));
        assertEquals("/videos?part=snippet,contentDetails,statistics"
                + "&id=abcdefghijk&key=test-key", requests.get(1));
    }

    /** An empty search makes no details request. */
    @Test
    public void handlesNoResultsAndRemovedVideo()
    {
        searchBody = "{\"items\":[]}";
        assertEquals("No YouTube videos found.", client.search("none"));
        assertEquals(1, requests.size());
        searchBody = "{\"items\":[{\"id\":{\"videoId\":\"abcdefghijk\"}}]}";
        detailsBody = "{\"items\":[]}";
        assertTrue(client.search("removed").contains("no longer available"));
    }

    /** Missing statistics are distinct from zero. */
    @Test
    public void handlesMissingMetadataAndZeroCounts()
    {
        video.remove("contentDetails");
        video.getJSONObject("snippet").remove("publishedAt");
        video.getJSONObject("statistics").remove("likeCount");
        video.getJSONObject("statistics").put("viewCount", "0");
        String result = client.search("query");
        assertTrue(result.contains("Duration: unavailable"));
        assertTrue(result.contains("Published: unavailable"));
        assertTrue(result.contains("Views: 0 | Likes: unavailable"));
        video.remove("statistics");
        assertTrue(client.search("query")
                .contains("Views: unavailable | Likes: unavailable"));
    }

    /** Durations include hours and streams have meaningful labels. */
    @Test
    public void formatsLongDurationsAndLiveVideos()
    {
        video.getJSONObject("contentDetails").put("duration", "P1DT2H3M4S");
        assertTrue(client.search("query").contains("Duration: 26:03:04"));
        video.getJSONObject("snippet").put("liveBroadcastContent", "live");
        assertTrue(client.search("query").contains("Duration: LIVE"));
        video.getJSONObject("snippet")
                .put("liveBroadcastContent", "upcoming");
        assertTrue(client.search("query").contains("Duration: UPCOMING"));
    }

    /** Titles cannot inject IRC controls or overflow a normal IRC line. */
    @Test
    public void sanitizesAndBoundsUnicodeOutput()
    {
        video.getJSONObject("snippet").put("title",
                "hi\r\n\u0001\u0003\u2028" + "🎸".repeat(200));
        String result = client.search("query");
        assertFalse(result.matches("(?s).*[\\p{Cc}\\p{Zl}\\p{Zp}].*"));
        assertFalse(result.contains("\ufffd"));
        assertTrue(result.getBytes(StandardCharsets.UTF_8).length < 400);
        assertTrue(result.endsWith("Likes: 678"));
    }

    /** Missing keys never generate API traffic. */
    @Test
    public void skipsRequestsWithoutConfiguration()
    {
        for (String key : new String[] {null, "", "  ", "${env:UNSET}"}) {
            assertTrue(new YouTubeSearch(key, endpoint()).search("query")
                    .contains("not configured"));
        }
        assertTrue(requests.isEmpty());
    }

    /** HTTP errors and malformed responses yield safe failure messages. */
    @Test
    public void handlesFailuresWithoutLeakingCredentials()
    {
        for (int code : new int[] {302, 400, 403, 429, 500}) {
            status = code;
            String reply = client.search("query");
            assertTrue(reply.contains("YouTube search failed"));
            assertFalse(reply.contains("test-key"));
        }
        status = 200;
        for (String body : new String[] {"not json", "{}",
                "{\"items\":[{\"id\":{\"videoId\":\"bad\\nID\"}}]}"}) {
            searchBody = body;
            assertTrue(client.search("query").contains("search failed"));
        }
        server.stop(0);
        assertTrue(client.search("query").contains("search failed"));
    }
}
