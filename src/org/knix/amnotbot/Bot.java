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
package org.knix.amnotbot;

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
public class Bot extends Thread implements IBot
{

    private String server;
    private int port;
    private List<String> channels;
    private static final int SO_TIMEOUT = 1000 * 60 * 5;
    private BotLogger logger;
    private BotConnection conn;

    public Bot(String server, List<String> channels)
    {
        new Bot(server, 0, channels);
    }

    public Bot(String server, int port, List<String> channels)
    {
        this.server = server;
        this.port = port;
        this.channels = channels;
        this.logger = new BotLogger(server);
        this.conn = null;

        start();
    }

    public void shutdown()
    {
        this.conn.doQuit();
    }

    public void run()
    {
        try {
            this.conn = createConnection(server, port, channels, logger);
            this.conn.connect();

            for (;;) {
                if (!this.conn.isConnected()) {
                    this.conn = createConnection(server, port, channels,
                                                                    logger);
                    this.conn.connect();
                }
                Thread.sleep(5000);
            }
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug(e);
        } catch (InterruptedException e) {
            BotLogger.getDebugLogger().debug(e);
        }
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
        bconn.addIRCEventListener(new BotListener(bconn, channels));
        bconn.setPong(true);
        bconn.setDaemon(false);
        bconn.setTimeout(SO_TIMEOUT);
        bconn.setEncoding("UTF-8");

        return bconn;
    }
}
