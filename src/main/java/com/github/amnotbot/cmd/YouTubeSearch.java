package com.github.amnotbot.cmd;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/** Retrieves a single relevant video using the YouTube Data API v3. */
public class YouTubeSearch
{
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    private final String key;
    private final String endpoint;

    /** Creates a search client for the official API. */
    public YouTubeSearch(String key)
    {
        this(key, "https://www.googleapis.com/youtube/v3");
    }

    /** Allows a local HTTP endpoint in integration tests. */
    YouTubeSearch(String key, String endpoint)
    {
        this.key = key;
        this.endpoint = endpoint;
    }

    /** Returns one IRC-safe result, or an actionable error message. */
    public String search(String query)
    {
        if (key == null || key.trim().isEmpty() || key.contains("${")) {
            return "YouTube search is not configured (youtube_api_key).";
        }
        try {
            JSONObject search = get("/search?part=snippet&type=video"
                    + "&order=relevance&maxResults=1&q=" + encode(query));
            JSONArray results = search.getJSONArray("items");
            if (results.length() == 0) {
                return "No YouTube videos found.";
            }
            String id = results.getJSONObject(0).getJSONObject("id")
                    .getString("videoId");
            if (!id.matches("[A-Za-z0-9_-]{11}")) {
                throw new IOException("Invalid video ID");
            }
            JSONObject details = get("/videos?part=snippet,contentDetails,"
                    + "statistics&id=" + encode(id));
            JSONArray videos = details.getJSONArray("items");
            if (videos.length() == 0) {
                return "The top YouTube video is no longer available.";
            }
            return format(id, videos.getJSONObject(0));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "YouTube search was interrupted. Please try again.";
        } catch (Exception e) {
            /* Do not expose request URLs, credentials, or API responses. */
            return "YouTube search failed. Please try again later; "
                    + "the API key or quota may need checking.";
        }
    }

    private JSONObject get(String path)
            throws IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path + "&key=" + encode(key)))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json").GET().build();
        HttpResponse<String> response = CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("YouTube request failed");
        }
        return new JSONObject(response.body());
    }

    private static String encode(String value)
    {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String format(String id, JSONObject video)
    {
        JSONObject snippet = video.getJSONObject("snippet");
        JSONObject details = video.optJSONObject("contentDetails");
        JSONObject statistics = video.optJSONObject("statistics");
        String title = snippet.getString("title")
                .replaceAll("[\\p{Cc}\\p{Zl}\\p{Zp}]", " ").trim();
        /* Keep Unicode titles within a conservative IRC byte budget. */
        while (title.getBytes(StandardCharsets.UTF_8).length > 180) {
            title = title.substring(0, title.offsetByCodePoints(
                    title.length(), -1));
        }
        String live = snippet.optString("liveBroadcastContent");
        String duration = "live".equals(live) ? "LIVE"
                : "upcoming".equals(live) ? "UPCOMING"
                : duration(details == null ? ""
                        : details.optString("duration"));
        String date = "unavailable";
        try {
            date = Instant.parse(snippet.optString("publishedAt"))
                    .atOffset(ZoneOffset.UTC).toLocalDate().toString();
        } catch (DateTimeParseException e) {
            /* Missing or invalid publication dates remain unavailable. */
        }
        return "https://www.youtube.com/watch?v=" + id + " | " + title
                + " | Duration: " + duration + " | Published: " + date
                + " | Views: " + count(statistics, "viewCount")
                + " | Likes: " + count(statistics, "likeCount");
    }

    private static String count(JSONObject statistics, String field)
    {
        String value = statistics == null ? "" : statistics.optString(field);
        return value.matches("[0-9]{1,20}") ? value : "unavailable";
    }

    private static String duration(String value)
    {
        try {
            long seconds = Duration.parse(value).getSeconds();
            if (seconds < 0) {
                return "unavailable";
            }
            return seconds >= 3600
                    ? String.format(Locale.ROOT, "%d:%02d:%02d",
                            seconds / 3600, seconds / 60 % 60, seconds % 60)
                    : String.format(Locale.ROOT, "%d:%02d",
                            seconds / 60, seconds % 60);
        } catch (DateTimeParseException | ArithmeticException e) {
            return "unavailable";
        }
    }
}
