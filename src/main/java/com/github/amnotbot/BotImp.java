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
package com.github.amnotbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.config.BotConfigurationUtils;
import com.github.amnotbot.proto.BotConnectionFactory;

/*
 * TODO - allow user input.
 */
/**
 * Very simple IRC bot.
 * 
 * @author Jimmy Mitchener &lt;jimmy.mitchener | et | [g]mail.com&gt;
 * @version 0.01
 */
public class BotImp extends Thread implements Bot
{

    private boolean running;
    private String protocol;
    private Configuration config;
    private final BotConnectionFactory factory;
    private Map<BotConnection, String> connections;

    public BotImp(String protocol, Configuration config)
    {
        this.running = true;
        this.config = config;
        this.protocol = protocol;
        this.factory = new BotConnectionFactory();
        this.connections = new HashMap<BotConnection, String>();

        this.initConnections();

        start();
    }

    public Configuration getConfig(BotConnection conn)
    {
        String server = this.connections.get(conn);
        return this.config.subset(this.protocol + "." + server);
    }

    public void shutdown()
    {
        this.running = false;
        for (BotConnection conn : this.connections.keySet()) {
            if (conn.isConnected()) {
                conn.doQuit();
            }
        }
    }

    @Override
    public void run()
    {         
        while (this.running) {
            try {
                this.checkConnections();
            } catch (IOException e) {
                BotLogger.getDebugLogger().debug(e);
            }
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
    }

    private void initConnections()
    {
        ArrayList<String> servers =
                BotConfigurationUtils.getRoots(config);
        Iterator<String> it = servers.iterator();
        while (it.hasNext()) {
            String server = it.next();
            BotConnection conn = null;
            try {
                conn = this.createConnection(server);
                this.connections.put(conn, server);
            } catch (IOException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
    }

    private void checkConnections() throws IOException
    {
        Map<BotConnection, String> connectionsCopy;
        
        connectionsCopy = this.connections;
        for (BotConnection conn : connectionsCopy.keySet()) {
            if (!conn.isConnected()) {
                String server = connectionsCopy.get(conn);
                BotConnection nconn = this.createConnection(server);
                this.connections.put(nconn, server);
                this.connections.remove(conn);
                conn = null;
            }
        }
    }

    private BotConnection createConnection(String server) throws IOException
    {
        BotConnection conn;

        conn = this.factory.createConnection(this.protocol,
                this.config.subset(server));
        conn.connect();

        return conn;
    }
    
}
