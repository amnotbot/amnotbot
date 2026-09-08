package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommandEvent;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.DummyConnection;
import com.github.amnotbot.config.BotConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;

/** Verifies command registration, usage, and channel replies. */
public class YouTubeCommandTest
{
    /** The shipped trigger handles only the intended command. */
    @Test
    public void configuredTriggerMatchesCommandBoundaries() throws Exception
    {
        Configuration commands = new PropertiesConfiguration(
                "src/main/resources/commands.config");
        BotCommandEvent event = new BotCommandEvent(
                commands.getString("YouTubeCommand"));
        assertTrue(event.test("!yt guitar heroe"));
        assertTrue(event.test("!YT\tguitar"));
        assertTrue(event.test("!yt"));
        assertFalse(event.test("!ytother guitar"));
        assertFalse(event.test("someone said !yt guitar"));
        assertTrue(Class.forName("com.github.amnotbot.cmd.YouTubeCommand")
                .getConstructor().newInstance() instanceof YouTubeCommand);
    }

    /** Empty queries show help and missing configuration replies once. */
    @Test
    public void repliesToOriginatingChannel()
    {
        BotConfiguration.setHomeDir("target/test-classes");
        Configuration config = BotConfiguration.getConfig();
        Object previous = config.getProperty("youtube_api_key");
        List<String> replies = new ArrayList<>();
        BotMessage message = new BotMessage(new DummyConnection() {
            @Override
            public void doPrivmsg(String target, String text)
            {
                replies.add(target + " " + text);
            }
        }, "#music", null, "!yt   ");
        try {
            config.clearProperty("youtube_api_key");
            YouTubeCommand command = new YouTubeCommand();
            command.execute(message);
            assertEquals(List.of("#music " + command.help()), replies);
            replies.clear();
            message.setText("!YT guitar heroe");
            command.execute(message);
            assertEquals(List.of("#music YouTube search is not configured "
                    + "(youtube_api_key)."), replies);
            replies.clear();
            message.setText("!ytother guitar");
            command.execute(message);
            assertTrue(replies.isEmpty());
        } finally {
            config.clearProperty("youtube_api_key");
            if (previous != null) {
                config.setProperty("youtube_api_key", previous);
            }
        }
    }
}
