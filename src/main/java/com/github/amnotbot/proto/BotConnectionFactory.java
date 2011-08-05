package com.github.amnotbot.proto;

import java.util.List;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotLogger;
import com.github.amnotbot.proto.irc.IRCBotConnection;
import com.github.amnotbot.proto.irc.IRCBotListener;

/**
 *
 * @author gpoppino
 */
public class BotConnectionFactory
{
    private static final int SO_TIMEOUT = 1000 * 60 * 5;

    public BotConnection createConnection(String protocol, Configuration config)
    {
        BotConnection conn = null;

        if (protocol.equals("irc")) {
            conn = this.createIRCConnection(
                    config.getString("server"),
                    config.getInt("port", 6667),
                    config.getList("channels")
                    );
        }

        return conn;
    }

    private BotConnection createIRCConnection(String server, int port,
            List<String> channels)
    {
        IRCBotConnection conn = null;

        if (port > 0) {
            conn = new IRCBotConnection(server, port);
        } else {
            conn = new IRCBotConnection(server);
        }

        conn.setBotLogger(new BotLogger(server));
        conn.addIRCEventListener(new IRCBotListener(conn, channels));
        conn.setTimeout(SO_TIMEOUT);
        conn.setEncoding("UTF-8");

        return conn;
    }

}
