/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.amnotbot.proto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotLogger;
import com.github.amnotbot.proto.irc.IRCBotConnection;
import com.github.amnotbot.proto.irc.IRCBotListener;
import com.github.amnotbot.proto.ircv3.IRCv3BotConnection;
import com.github.amnotbot.proto.xmpp.XMPPBotConnection;
import org.jivesoftware.smack.ConnectionConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotConnectionFactory
{
    private static final int SO_TIMEOUT = 1000 * 60 * 5;

    public BotConnection createConnection(final String protocol, final Configuration config)
    {
        BotConnection conn = null;

        switch (protocol.toLowerCase()) {
            case "irc":
                conn = this.createIRCConnection(
                    config.getString("server"),
                    config.getInt("port", 6667),
                    Arrays.asList(config.getStringArray("channels")),
                    config.getBoolean("ssl")
                );
                break;
            case "ircv3":
                conn = this.createIRCv3Connection(
                    config.getString("server"),
                    config.getInt("port", 6667),
                    Arrays.asList(config.getStringArray("channels")),
                    config.getBoolean("ssl")
                );
                break;
            case "xmpp":
                conn = this.createXMPPConnection(config);
                break;
        }

        return conn;
    }

    private BotConnection createIRCv3Connection(final String server, final int port, final List<String> channels, final boolean ssl) {
        IRCv3BotConnection conn = new IRCv3BotConnection(server, port, channels, ssl);

        conn.setBotLogger(new BotLogger(server));

        return conn;
    }

    private BotConnection createIRCConnection(final String server, final int port,
            final List<String> channels, final Boolean ssl)
    {
        IRCBotConnection conn = null;

        if (ssl) {
            if (port > 0) {
                conn = new IRCBotConnection(server, port, true);
            } else {
                conn = new IRCBotConnection(server, true);
            }
        } else {
            if (port > 0) {
                conn = new IRCBotConnection(server, port, false);
            } else {
                conn = new IRCBotConnection(server, false);
            }
        }

        conn.setBotLogger(new BotLogger(server));
        conn.addIRCEventListener(new IRCBotListener(conn, channels));
        conn.setTimeout(SO_TIMEOUT);
        conn.setEncoding("UTF-8");

        return conn;
    }

    private BotConnection createXMPPConnection(final Configuration config)
    {
        XMPPBotConnection conn = null;

        final ConnectionConfiguration connConfig = new ConnectionConfiguration(config.getString("server"), 
                config.getInt("port", 5222));
        connConfig.setCompressionEnabled(true);
        connConfig.setSASLAuthenticationEnabled(false);
        connConfig.setSelfSignedCertificateEnabled(true);

        conn = new XMPPBotConnection(connConfig,
                config.getString("user"),
                config.getString("password"),
                config.getString("resource"),
                Arrays.asList(config.getStringArray("channels"))
                );
        conn.setBotLogger(new BotLogger(config.getString("server")));

        return conn;
    }

}
