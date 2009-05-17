/*
 * Copyright (c) 2007 Geronimo Poppino
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCUser;

class SpamConstants
{

    public SpamConstants()
    {
    }
    /** milliseconds **/
    public static final int MIN_DIFF_ALLOWED = 1000 * 3;
    public static final int MAX_QUERIES_PER_UNIT_TIME = 3;
    public static final int GLOBAL_MAX_QUERIES_PER_UNIT_TIME = 10;
    public static final int UNIT_TIME = 1000 * 60;
    public static final int GLOBAL_UNIT_TIME = 1000 * 60;
};

class QueryTime
{

    private long time;

    QueryTime(long time)
    {
        this.time = time;
    }

    public long getQueryTime()
    {
        return this.time;
    }
};

class AmnotbotUser
{

    public LinkedList<QueryTime> queriesQueue;
    private String nick;

    public AmnotbotUser(String nick)
    {
        this.nick = nick;
        this.queriesQueue = new LinkedList<QueryTime>();
    }

    public String getNick()
    {
        return this.nick;
    }
};

class ChannelSpamDetector
{

    private LinkedList<QueryTime> globalQueriesQueue;
    private Hashtable<String, AmnotbotUser> queriesPerUser;
    private long queryTime;

    public ChannelSpamDetector()
    {
        this.globalQueriesQueue = new LinkedList<QueryTime>();
        this.queriesPerUser = new Hashtable<String, AmnotbotUser>();
    }

    public void setQueryTime(long time)
    {
        this.queryTime = time;
    }

    public long getQueryTime()
    {
        return this.queryTime;
    }

    public boolean checkForSpam(IRCUser user)
    {
        this.setQueryTime(System.currentTimeMillis());

        if (this.checkGlobalQueries()) return true;

        if (this.checkQueriesPerUser(user)) return true;

        return false;
    }

    private boolean checkGlobalQueries()
    {
        if (this.globalQueriesQueue.isEmpty()) {
            this.globalQueriesQueue.offer(new QueryTime(this.getQueryTime()));
            return false;
        }

        if (this.checkGlobalMinGap()) return true;

        if (this.checkGlobalMaxQueriesPerUnitTime()) return true;
        
        return false;
    }

    private boolean checkGlobalMinGap()
    {
        QueryTime lastQuery;
        lastQuery = (QueryTime) this.globalQueriesQueue.getLast(); // tail
        long diff = this.getQueryTime() - lastQuery.getQueryTime();

        if (SpamConstants.MIN_DIFF_ALLOWED > diff) return true;

        return false;
    }

    private boolean checkGlobalMaxQueriesPerUnitTime()
    {
        int qSize = this.globalQueriesQueue.size();
        if (qSize < SpamConstants.GLOBAL_MAX_QUERIES_PER_UNIT_TIME) {
            this.globalQueriesQueue.offer(new QueryTime(this.getQueryTime()));
            return false;
        }

        QueryTime firstQuery = this.globalQueriesQueue.poll();
        long diff = this.getQueryTime() - firstQuery.getQueryTime();

        this.globalQueriesQueue.offer(new QueryTime(this.getQueryTime()));
        if (SpamConstants.GLOBAL_UNIT_TIME > diff) return true;

        while (this.globalQueriesQueue.size() > 1) {
            this.globalQueriesQueue.remove();
        }
        return false;
    }

    private boolean checkQueriesPerUser(IRCUser user)
    {
        AmnotbotUser amnotbotUser;
        amnotbotUser = (AmnotbotUser) this.queriesPerUser.get(user.getNick());

        if (amnotbotUser == null) {
            amnotbotUser = new AmnotbotUser(user.getNick());

            amnotbotUser.queriesQueue.offer(new QueryTime(this.getQueryTime()));
            this.queriesPerUser.put(user.getNick(), amnotbotUser);

            return false;
        }

        if (this.checkPerUserMinGap(amnotbotUser)) return true;
        
        if (this.checkPerUserMaxQueriesPerUnitTime(amnotbotUser)) return true;        

        return false;
    }

    private boolean checkPerUserMinGap(AmnotbotUser amnotbotUser)
    {
        QueryTime lastQuery;
        lastQuery = (QueryTime) amnotbotUser.queriesQueue.getLast(); // tail
        long diff = this.getQueryTime() - lastQuery.getQueryTime();

        if (SpamConstants.MIN_DIFF_ALLOWED > diff) return true;
        return false;
    }

    private boolean checkPerUserMaxQueriesPerUnitTime(AmnotbotUser amnotbotUser)
    {
        int qSize = amnotbotUser.queriesQueue.size();
        if (qSize < SpamConstants.MAX_QUERIES_PER_UNIT_TIME) {
            amnotbotUser.queriesQueue.offer(new QueryTime(this.getQueryTime()));
            return false;
        }

        QueryTime firstQuery = amnotbotUser.queriesQueue.poll();
        long diff = this.getQueryTime() - firstQuery.getQueryTime();

        amnotbotUser.queriesQueue.offer(new QueryTime(this.getQueryTime()));
        if (SpamConstants.UNIT_TIME > diff) return true;

        while (amnotbotUser.queriesQueue.size() > 1) {
            amnotbotUser.queriesQueue.remove();
        }
        return false;
    }
}

public class BotSpamDetector
{

    private Hashtable<String, ChannelSpamDetector> chanSpamDetector;

    public BotSpamDetector(List<String> channels)
    {
        this.chanSpamDetector = new Hashtable<String, ChannelSpamDetector>();

        String chan;
        Iterator<String> it = channels.iterator();
        while (it.hasNext()) {
            chan = it.next();
            this.addChannel(chan);
        }
    }

    public BotSpamDetector()
    {
        this.chanSpamDetector = new Hashtable<String, ChannelSpamDetector>();
    }

    public void addChannel(String chan)
    {
        if (!this.chanSpamDetector.contains(chan)) {
            this.chanSpamDetector.put(chan, new ChannelSpamDetector());
        }
    }

    public void removeChannel(String chan)
    {
        this.chanSpamDetector.remove(chan);
    }

    public boolean checkForSpam(String channel, IRCUser user)
    {
        ChannelSpamDetector spamDetector;

        spamDetector = this.chanSpamDetector.get(channel);
        if (spamDetector == null) return false;
           
        if (spamDetector.checkForSpam(user)) return true;
        return false;
    }
}

class IRCListenerSpamDetectorAdapter extends IRCEventAdapter
{

    private BotConnection conn;
    private BotSpamDetector spamDetector;    

    public IRCListenerSpamDetectorAdapter(
            BotSpamDetector spamDetector,
            BotConnection conn)
    {
        this.conn = conn;
        this.spamDetector = spamDetector;
    }

    public void onJoin(String chan, IRCUser user)
    {
        if (this.conn.getNick().equals( user.getNick() )) {
           this.spamDetector.addChannel(chan);
        }
    }

    public void onPart(String chan, IRCUser user, String msg)
    {
        if (this.conn.getNick().equals( user.getNick() )) {
            this.spamDetector.removeChannel(chan);
        }
    }
    
}
