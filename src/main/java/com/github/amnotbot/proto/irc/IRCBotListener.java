/*
 * Copyright (c) 2007 Jimmy Mitchener <jimmy.mitchener@gmail.com>
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
package com.github.amnotbot.proto.irc;


import java.util.List;

import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.*;
import com.github.amnotbot.config.BotConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * Event handler for MainBot. This is the real workhorse.
 * @author Jimmy Mitchener
 *
 * 22-10-2007 gresco Implemented the "Command Pattern" for commands. 
 */
public class IRCBotListener implements IRCEventListener
{
    private IRCBotConnection conn;
    private List<String> channels;
    private BotCommandInterpreter cmdInterpreter;
    private BotTaskManager taskManager;

    /**
     * Create a new BotListener object
     * @param con IRC Connection we're handling
     * @param channels Channels to join on connect
     */
    public IRCBotListener(IRCBotConnection conn, List<String> channels)
    {
        this.conn = conn;
        this.channels = channels;

        BotCommandInterpreterConstructor c =
                new BotCommandInterpreterConstructor(
                                new IRCBotCommandInterpreterBuilderFile()
                );

        this.cmdInterpreter = c.construct(conn);
    }

    public void onPrivmsg(String target, IRCUser user, String msg)
    {
        conn.print(target, user.getNick() + "> " + msg);
        
        if (StringUtils.equals(target, this.conn.getNick())) {
            target = user.getNick();
        }

        this.cmdInterpreter.run( 
                new BotMessage(this.conn, target, new IRCBotUser(user), msg) );
    }

    public void onDisconnected()
    {
        conn.print(BotConstants.getBotConstants().getAppPFX() +
                " DISCONNECTED!");
        // if it has registered
        if (this.taskManager != null) this.taskManager.cancelTasks();
    }

    public void onError(int num, String msg)
    {
        conn.print(BotConstants.getBotConstants().getServerPFX() +
                " ERROR #" + num + " !!!!!");
        conn.print("\t" + msg);

        switch (num) {
            case 432:
            case 433:
                String nick = this.conn.getNick();

                if (nick.endsWith("_")) {
                    this.conn.doNick(nick.substring(0, nick.length()-1) + "-");
                } else {
                    this.conn.doNick(nick + "_");
                }
                break;
        }
    }

    public void onError(String msg)
    {
        conn.print(BotConstants.getBotConstants().getServerPFX() + " ERROR!!!!");
        conn.print("\t" + msg);
    }

    public void onInvite(String chan, IRCUser user, String pNick)
    {
        conn.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + pNick + " invited " +
                ((pNick.compareTo(conn.getNick()) == 0) ? "you" : pNick) +
                " to join " + chan);
    }

    public void onJoin(String chan, IRCUser user)
    {
        conn.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + user.getNick() +
                " [" + user.getHost() + "] has joined " + chan);
    }

    public void onKick(String chan, IRCUser user, String pNick, String msg)
    {
        this.conn.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + user.getNick() + " [" + user.getHost() + "] has kicked " +
                pNick + " from " + chan + " with message [" + msg + "]");

        if ( pNick.equals(this.conn.getNick()) ) {
            if (BotConfiguration.getConfig().getBoolean("auto_rejoin")) {
                this.conn.doJoin(chan);
            }
        }
    }

    public void onNick(IRCUser nick, String newNick)
    {
        this.conn.print(BotConstants.getBotConstants().getServerPFX() +
                " " + nick.getNick() + " is now known as " + newNick);
    }

    public void onNotice(String target, IRCUser user, String msg)
    {
        this.conn.print(BotConstants.getBotConstants().getServerPFX() +
                " NOTICE " + user.getNick() + ": " + msg);
    }

    public void onPart(String chan, IRCUser user, String msg)
    {
        this.conn.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + user.getNick() + " [" + user.getHost() + "] has left " +
                chan + " [" + msg + "]");
    }

    public void onPing(String ping)
    {
        this.conn.print(BotConstants.getBotConstants().getServerPFX() +
                " PING: " + ping);
    }

    public void onQuit(IRCUser user, String msg)
    {
        this.conn.print(BotConstants.getBotConstants().getServerPFX() +
                " " + user + " [" + user.getHost() + "] has quit [" + msg + "]");
    }

    /**
     * Join channels supplied on init once registered.
     */
    public void onRegistered()
    {
        this.conn.print(BotConstants.getBotConstants().getServerPFX() +
                " SUCCESS: " + this.conn.getHost() + " connection registered");

        Configuration config = BotConfiguration.getConfig();
        if (config.getBoolean("nickserv_enabled")) {
            String nickserv = config.getString("nickserv");
            String passwd = config.getString("nickserv_password");
            this.conn.doPrivmsg(nickserv, "IDENTIFY " + passwd);
        }
        
        for (String channel : channels) {
            this.conn.doJoin(channel);
        }

        BotTaskConstructor tc = new BotTaskConstructor(new BotTaskBuilderFile());
        this.taskManager = tc.construct(this.conn, this.channels);
    }

    public void onReply(int num, String value, String msg) { }

    public void onMode(String chan, IRCUser user, IRCModeParser mParser)
    {
        /* 
         * TODO - actually use the parser instead of calling getLine()
         */
        this.conn.print(chan,
                BotConstants.getBotConstants().getServerPFX() +
                " mode/" + chan + " [" + mParser.getLine() + "] " +
                "by " + user.getNick());
    }

    public void onMode(IRCUser user, String pNick, String mode)
    {
        this.conn.print(user.getNick() + " has changed user mode for " +
                (user.getNick().equals(pNick) ? "himself" : pNick) +
                " to " + mode);
    }

    public void onTopic(String chan, IRCUser user, String topic)
    {
        this.conn.print(chan, user.getNick() +
                " has changed the topic to: " + topic);
    }

    public void unknown(String pfx, String cmd, String middle, String end)
    {
        this.conn.print(BotConstants.getBotConstants().getAppPFX() +
                " Received UNKNOWN Event: " + cmd);
        this.conn.print("\tprefix: '" + pfx + "'");
        this.conn.print("\tmiddle: '" + middle + "'");
        this.conn.print("\tend:    '" + end + "'");
    }
}
