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
