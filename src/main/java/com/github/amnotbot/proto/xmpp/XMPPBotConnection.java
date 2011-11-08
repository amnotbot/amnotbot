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
package com.github.amnotbot.proto.xmpp;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotLogger;
import com.github.amnotbot.config.BotConfiguration;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCInitialPresence;

/**
 *
 * @author gpoppino
 */
public class XMPPBotConnection implements BotConnection
{

    private BotLogger logger;
    private XMPPConnection conn;
    private MessageListener messageListener;
    private PacketListener packetListener;
    private HashMap<String, Chat> chats;
    private HashMap<String, MultiUserChat> muchats;
    private List<String> channels;
    private String user;
    private String passwd;
    private String resource;
    
    public XMPPBotConnection(ConnectionConfiguration config, 
            String user, 
            String passwd, 
            String resource, 
            List<String> channels)
    {
        this.conn = new XMPPConnection(config);
        this.user = user;
        this.passwd = passwd;
        this.resource = resource;
        this.channels = channels;
        this.chats = new HashMap<String, Chat>();
        this.muchats = new HashMap<String, MultiUserChat>();
        this.messageListener = new XMPPBotMessageListener(this);
        this.packetListener = new XMPPBotPacketListener(this);
    }
    
    @Override
    public void connect() throws IOException 
    {
        try {
            this.conn.connect();
            this.conn.login(this.user, this.passwd, this.resource);
        } catch (XMPPException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            throw new IOException();
        }
        ChatManager m = this.conn.getChatManager();
        m.addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) 
                    {
                       if (!createdLocally) {
                           chat.addMessageListener(messageListener);
                           chats.put(chat.getParticipant(), chat);
                       }
                    }
                });
        
        this.joinRooms();
    }
    
    private void joinRooms()
    {
        for (String chan : channels) {
            this.doJoin(chan);
        }
    }

    @Override
    public void doPrivmsg(String target, String msg) 
    {
        Chat chat;
        MultiUserChat muchat;
        
        chat = this.chats.get(target);
        muchat = this.muchats.get(target);
        System.out.println("target: " + target);
        System.out.println("msg: " + msg);
        try {
            if (chat != null) chat.sendMessage(msg);
            if (muchat != null) muchat.sendMessage(msg);
        } catch (XMPPException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
    }

    @Override
    public void doNick(String nick) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doQuit() 
    {
        this.conn.disconnect();
    }

    @Override
    public void doJoin(String room) 
    {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxChars(0);
        
        MultiUserChat muchat = new MultiUserChat(this.conn, room);
        muchat.addMessageListener(this.packetListener);
        try {
            muchat.join(BotConfiguration.getConfig().getString("nick"), null, history, 10000);
        } catch (XMPPException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
        this.muchats.put(room, muchat);
    }

    @Override
    public boolean isConnected() 
    {
        return this.conn.isConnected();
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
    public void setTimeout(int millis) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEncoding(String encoding) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBotLogger(BotLogger logger) 
    {
        this.logger = logger;
    }

    @Override
    public BotLogger getBotLogger() 
    {
        return this.logger;
    }

    @Override
    public String getHost() 
    {
        return this.conn.getHost();
    }

    @Override
    public String getNick() 
    {
        return this.conn.getUser();
    }
    
}
