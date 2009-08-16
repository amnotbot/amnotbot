/*
 * Copyright (c) 2008 Jimmy Mitchener <jcm@packetpan.org>
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
package org.knix.amnotbot.proto.irc;

import org.knix.amnotbot.*;
import java.io.IOException;
import java.util.List;

/*
 * TODO - allow for multiple servers at startup
 *      - allow user input.
 */
/**
 * Very simple IRC bot.
 * 
 * @author Jimmy Mitchener &lt;jimmy.mitchener | et | [g]mail.com&gt;
 * @version 0.01
 */
public class IRCBot extends Thread implements Bot
{

    private String server;
    private int port;
    private List<String> channels;
    private static final int SO_TIMEOUT = 1000 * 60 * 5;
    private BotLogger logger;
    private BotConnection conn;
    private boolean running;

    public IRCBot(String server, List<String> channels)
    {
        new IRCBot(server, 0, channels);
    }

    public IRCBot(String server, int port, List<String> channels)
    {
        this.server = server;
        this.port = port;
        this.channels = channels;
        this.running = true;
        this.logger = new BotLogger(server);
        this.conn = null;

        start();
    }

    public void shutdown()
    {
        this.running = false;
        if (this.conn.isConnected()) {
            this.conn.doQuit();
        }
    }

    public void run()
    {         
        while (this.running) {
            if (!this.checkConnection()) {
                try {
                    this.startConnection();
                } catch (IOException e) {
                    BotLogger.getDebugLogger().debug(e);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
    }    

    private boolean checkConnection()
    {
        if (this.conn == null) return false;
        if (!this.conn.isConnected()) return false;
        return true;
    }

    private void startConnection() throws IOException
    {
        this.conn = null;
        this.conn = createConnection(server, port, channels, this.logger);
        this.conn.connect();
    }

    private static BotConnection createConnection(String server,
            int port,
            List<String> channels,
            BotLogger logger)
    {
        BotConnection bconn = null;

        if (port > 0) {
            bconn = new BotConnection(server, port);
        } else {
            bconn = new BotConnection(server);
        }
        
        bconn.setBotLogger(logger);
        bconn.addIRCEventListener(new IRCBotListener(bconn, channels));
        bconn.setPong(true);
        bconn.setDaemon(false);
        bconn.setTimeout(SO_TIMEOUT);
        bconn.setEncoding("UTF-8");

        return bconn;
    }
}
