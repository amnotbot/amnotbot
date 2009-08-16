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

package org.knix.amnotbot.spam;

import java.util.Hashtable;
import java.util.LinkedList;

import org.knix.amnotbot.BotUser;

public class ChannelSpamDetector
{
    private LinkedList<Long> globalQueriesQueue;
    private Hashtable<String, AmnotbotUser> queriesPerUser;
    private long queryTime;

    public ChannelSpamDetector()
    {
        this.globalQueriesQueue = new LinkedList<Long>();
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

    public boolean checkForSpam(BotUser user)
    {
        this.setQueryTime(System.currentTimeMillis());

        if (this.checkGlobalQueries()) return true;

        if (this.checkQueriesPerUser(user)) return true;

        return false;
    }

    private boolean checkGlobalQueries()
    {
        if (this.globalQueriesQueue.isEmpty()) {
            this.globalQueriesQueue.offer(this.getQueryTime());
            return false;
        }

        if (this.checkGlobalMinGap()) return true;

        if (this.checkGlobalMaxQueriesPerUnitTime()) return true;
        
        return false;
    }

    private boolean checkGlobalMinGap()
    {
        Long lastQuery;
        lastQuery = this.globalQueriesQueue.getLast(); // tail
        long diff = this.getQueryTime() - lastQuery;

        if (SpamConstants.MIN_DIFF_ALLOWED > diff) return true;

        return false;
    }

    private boolean checkGlobalMaxQueriesPerUnitTime()
    {
        int qSize = this.globalQueriesQueue.size();
        if (qSize < SpamConstants.GLOBAL_MAX_QUERIES_PER_UNIT_TIME) {
            this.globalQueriesQueue.offer(this.getQueryTime());
            return false;
        }

        Long firstQuery = this.globalQueriesQueue.poll();
        long diff = this.getQueryTime() - firstQuery;

        this.globalQueriesQueue.offer(this.getQueryTime());
        if (SpamConstants.GLOBAL_UNIT_TIME > diff) return true;

        while (this.globalQueriesQueue.size() > 1) {
            this.globalQueriesQueue.remove();
        }
        return false;
    }

    private boolean checkQueriesPerUser(BotUser user)
    {
        AmnotbotUser amnotbotUser;
        amnotbotUser = (AmnotbotUser) this.queriesPerUser.get(user.getNick());

        if (amnotbotUser == null) {
            amnotbotUser = new AmnotbotUser(user.getNick());

            amnotbotUser.getQueriesQueue().offer(this.getQueryTime());
            this.queriesPerUser.put(user.getNick(), amnotbotUser);

            return false;
        }

        if (this.checkPerUserMinGap(amnotbotUser)) return true;
        
        if (this.checkPerUserMaxQueriesPerUnitTime(amnotbotUser)) return true;        

        return false;
    }

    private boolean checkPerUserMinGap(AmnotbotUser amnotbotUser)
    {
        Long lastQuery;
        lastQuery = amnotbotUser.getQueriesQueue().getLast(); // tail
        long diff = this.getQueryTime() - lastQuery;

        if (SpamConstants.MIN_DIFF_ALLOWED > diff) return true;
        return false;
    }

    private boolean checkPerUserMaxQueriesPerUnitTime(AmnotbotUser amnotbotUser)
    {
        int qSize = amnotbotUser.getQueriesQueue().size();
        if (qSize < SpamConstants.MAX_QUERIES_PER_UNIT_TIME) {
            amnotbotUser.getQueriesQueue().offer(this.getQueryTime());
            return false;
        }

        Long firstQuery = amnotbotUser.getQueriesQueue().poll();
        long diff = this.getQueryTime() - firstQuery;

        amnotbotUser.getQueriesQueue().offer(this.getQueryTime());
        if (SpamConstants.UNIT_TIME > diff) return true;

        while (amnotbotUser.getQueriesQueue().size() > 1) {
            amnotbotUser.getQueriesQueue().remove();
        }
        return false;
    }
}