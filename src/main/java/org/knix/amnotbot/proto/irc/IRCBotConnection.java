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

import java.io.IOException;
import org.knix.amnotbot.BotConnection;
import org.knix.amnotbot.BotConstants;
import org.knix.amnotbot.BotLogger;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventListener;

public class IRCBotConnection implements BotConnection
{

    private BotLogger logger;
    private boolean silent = false;
    private IRCConnection conn;

    public IRCBotConnection(String server,
            int[] ports,
            String passwd,
            String nick,
            String user,
            String name)
    {
        this.conn = new IRCConnection(server, ports, passwd, nick, user, name);

        this.init();
    }

    public IRCBotConnection(String server,
            int[] ports,
            String passwd,
            String nick)
    {
        this.conn = new IRCConnection(server, ports, passwd, nick, nick, nick);

        this.init();
    }

    public IRCBotConnection(String server,
            int[] ports,
            String nick)
    {
        this.conn = new IRCConnection(server, ports, null, nick, nick, nick);

        this.init();
    }

    public IRCBotConnection(String server, int port, String nick)
    {
        this.conn = 
                new IRCConnection(server, new int[]{port}, null, nick, nick,
                    nick);

        this.init();
    }

    public IRCBotConnection(String server, int port, String passwd, String nick)
    {
        this.conn = new IRCConnection(server, new int[]{port}, passwd, nick,
                nick, nick);

        this.init();
    }

    public IRCBotConnection(String server, String passwd, String nick)
    {
        this.conn = new IRCConnection(server,
                BotConstants.getBotConstants().getIrcPorts(), passwd, nick,
                nick, nick);

        this.init();
    }

    public IRCBotConnection(String server, String nick)
    {
        this.conn = new IRCConnection(server,
                BotConstants.getBotConstants().getIrcPorts(), null, nick, nick,
                nick);

        this.init();
    }

    public IRCBotConnection(String server)
    {
        this.conn = new IRCConnection(server,
                BotConstants.getBotConstants().getIrcPorts(),
                null,
                BotConstants.getBotConstants().getNick(),
                BotConstants.getBotConstants().getNick(),
                BotConstants.getBotConstants().getNick());

        this.init();
    }

    public IRCBotConnection(String server, int port)
    {
        this.conn = new IRCConnection(server, new int[]{port},
                null,
                BotConstants.getBotConstants().getNick(),
                BotConstants.getBotConstants().getNick(),
                BotConstants.getBotConstants().getNick());

        this.init();
    }

    private void init()
    {
        this.conn.setDaemon(false);
        this.conn.setPong(true);
    }

    @Override
    public void setBotLogger(BotLogger logger)
    {
        this.logger = logger;
    }

    @Override
    public BotLogger getBotLogger()
    {
        return logger;
    }

    @Override
    public void doPrivmsg(String target, String msg)
    {
        if (silent) return;

        this.conn.doPrivmsg(target, msg);

        this.print(target, this.conn.getNick() + "> " + msg);
    }

    @Override
    public void doNick(String nick)
    {
        print("doNick(" + nick + ") called.");
        this.conn.doNick(nick);
    }

    @Override
    public void print(String msg)
    {
        if (this.logger != null) {
            this.logger.log(msg);
        }
    }

    @Override
    public void print(String target, String msg)
    {
        if (this.logger != null) {
            this.logger.log(target.toLowerCase(), msg);
        }
    }

    @Override
    public void doQuit()
    {
        this.conn.doQuit();
    }

    @Override
    public boolean isConnected()
    {
        return this.conn.isConnected();
    }

    @Override
    public void connect() throws IOException
    {
        this.conn.connect();
    }

    // Probably should be part of a specific IRC Interface
    public void addIRCEventListener(IRCEventListener l)
    {
        this.conn.addIRCEventListener(l);
    }

    // Probably should be part of a specific IRC Interface
    public void removeIRCEventListener(IRCEventListener l)
    {
        this.conn.removeIRCEventListener(l);
    }

    @Override
    public void setTimeout(int millis)
    {
        this.conn.setTimeout(millis);
    }

    @Override
    public void setEncoding(String encoding)
    {
        this.conn.setEncoding(encoding);
    }

    @Override
    public String getHost()
    {
        return this.conn.getHost();
    }

    @Override
    public String getNick()
    {
        return this.conn.getNick();
    }

    @Override
    public void doJoin(String room)
    {
        this.conn.doJoin(room);
    }

}
