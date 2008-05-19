/*
 * Copyright (c) 2008 Jimmy Mitchener <jimmy.mitchener@gmail.com>
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

package org.knix.amnotbot.config;

import java.util.List;

/**
 * TODO - add action handlers for regex and commands
 * 
 * @author jmitchener
 */
public class BotConnectionConfigImpl implements BotConnectionConfig
{
    private String nick;
    private String realName;
    private String userName;
    private String password;
    
    private Integer port;
    private List<BotChannel> channels;
    
    public String getNick()
    {
        return nick;
    }

    public Integer getPort()
    {
        return port;
    }

    public String getRealName()
    {
        return realName;
    }

    public String getUserName()
    {
        return userName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public List<BotChannel> getChannels()
    {
        return channels;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }

    
    public void setChannels(List<BotChannel> channels)
    {
        this.channels = channels;
    }
}
