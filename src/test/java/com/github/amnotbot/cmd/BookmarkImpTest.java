package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;
import com.github.amnotbot.DummyConnection;
import com.github.amnotbot.config.BotConfiguration;
import org.apache.commons.configuration.Configuration;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookmarkImpTest
{
    private HttpServer server;
    private String backend;
    private volatile int status = 201;
    private final AtomicInteger requests = new AtomicInteger();
    private final AtomicReference<String> body = new AtomicReference<>();
    private final AtomicReference<String> cookie = new AtomicReference<>();
    private final AtomicReference<String> contentType = new AtomicReference<>();
    private final AtomicReference<String> method = new AtomicReference<>();
    private final List<String> announcements = new ArrayList<>();
    private BotMessage message;

    @Before
    public void setUp() throws Exception
    {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/bookmarks", exchange -> {
            requests.incrementAndGet();
            method.set(exchange.getRequestMethod());
            cookie.set(exchange.getRequestHeaders().getFirst("Cookie"));
            contentType.set(exchange.getRequestHeaders()
                    .getFirst("Content-Type"));
            body.set(new String(exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8));
            if (status == 302) {
                exchange.getResponseHeaders().set("Location", "/api/bookmarks");
            }
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
        backend = "http://127.0.0.1:" + server.getAddress().getPort();
        message = new BotMessage(new DummyConnection() {
            @Override
            public void doPrivmsg(String target, String text)
            {
                announcements.add(target + " " + text);
            }
        }, "#links", null, "");
    }

    @After
    public void tearDown()
    {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void encodesUrlAndAnnouncesSuccessfulSave()
    {
        String url = "https://my-site.technology/ñ?q=\"hi\"&path=\\a\t\n";
        new BookmarkImp(backend, "test-token", url).run(message);

        assertEquals(1, requests.get());
        assertEquals("POST", method.get());
        assertEquals("application/json", contentType.get());
        assertEquals("access_token=test-token", cookie.get());
        JSONObject payload = new JSONObject(body.get());
        assertEquals(1, payload.length());
        assertEquals(url, payload.getString("url"));
        assertEquals(List.of("#links Bookmarked: " + url), announcements);
    }

    @Test
    public void announcesEmptySuccessfulResponse()
    {
        status = 204;
        new BookmarkImp(backend, "token", "https://example.dev").run(message);
        assertEquals(1, announcements.size());
    }

    @Test
    public void missingConfigurationDoesNotSendOrAnnounce()
    {
        String[] missing = {null, "", " \t ", "${env:UNSET_BOOKMARK_VALUE}"};
        for (String value : missing) {
            new BookmarkImp(backend, value, "https://example.dev").run(message);
            new BookmarkImp(value, "token", "https://example.dev").run(message);
        }
        assertEquals(0, requests.get());
        assertTrue(announcements.isEmpty());
    }

    @Test
    public void failedResponsesDoNotAnnounce()
    {
        for (int failure : new int[] {302, 400, 401, 500}) {
            status = failure;
            new BookmarkImp(backend, "token", "https://example.dev").run(message);
        }
        assertEquals(4, requests.get());
        assertTrue(announcements.isEmpty());
    }

    @Test
    public void malformedBackendAndConnectionFailureDoNotAnnounce()
    {
        new BookmarkImp(":invalid", "token", "https://example.dev").run(message);
        server.stop(0);
        new BookmarkImp(backend, "token", "https://example.dev").run(message);
        assertTrue(announcements.isEmpty());
    }

    @Test
    public void commandHandlesAbsentConfigAndSavesFirstUrl()
    {
        BotConfiguration.setHomeDir("target/test-classes");
        Configuration config = BotConfiguration.getConfig();
        String backendKey = "bookmark_backend_url";
        String tokenKey = "bookmark_jwt_token";
        Object previousBackend = config.getProperty(backendKey);
        Object previousToken = config.getProperty(tokenKey);
        try {
            config.clearProperty(backendKey);
            config.clearProperty(tokenKey);
            message.setText("see https://my-site.technology/a and "
                    + "https://second.dev/b");
            BookmarkCommand command = new BookmarkCommand();
            command.execute(message);
            config.setProperty(backendKey, backend);
            command.execute(message);
            assertEquals(0, requests.get());
            assertTrue(announcements.isEmpty());

            config.setProperty(tokenKey, "token");
            command.execute(message);
            assertEquals(1, requests.get());
            assertEquals("https://my-site.technology/a",
                    new JSONObject(body.get()).getString("url"));
            assertEquals(List.of("#links Bookmarked: "
                    + "https://my-site.technology/a"), announcements);
        } finally {
            config.clearProperty(backendKey);
            config.clearProperty(tokenKey);
            if (previousBackend != null) {
                config.setProperty(backendKey, previousBackend);
            }
            if (previousToken != null) {
                config.setProperty(tokenKey, previousToken);
            }
        }
    }

    @Test
    public void messageWithoutUrlIsIgnored()
    {
        message.setText("There are no links here");
        new BookmarkCommand().execute(message);
        assertEquals(0, requests.get());
        assertTrue(announcements.isEmpty());
    }
}
