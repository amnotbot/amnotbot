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

import org.knix.amnotbot.proto.irc.IRCBot;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.config.BotConfiguration;

public class Main
{
    public static void main(String[] args)
    {
        Configuration config;
        config = BotConfiguration.getConfig();
        
        String server = null;
        List<String> channels = null;
        Configuration serverCfg = null;

        Iterator<String> it = config.subset("irc.server").getKeys();
        while (it.hasNext()) {
            String serverTag = it.next();
            
            server = config.getString("irc.server." + serverTag);
            serverCfg = config.subset("irc." + serverTag);
            channels = serverCfg.getList("channels");
            int port = serverCfg.getInt("port", 6667);

            Runtime.getRuntime().addShutdownHook(
                    new BotShutdownHandler(
                    new IRCBot(server, port, channels)));
        }
    }
}
