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
package com.github.amnotbot;

import java.io.IOException;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotLogger;

/**
 *
 * @author gpoppino
 */
public class DummyConnection implements BotConnection
{
    private String output;

    public DummyConnection()
    {
        this.output = null;
    }

    public void doPrivmsg(String target, String msg)
    {
        this.output = msg;
    }

    public String getOutput()
    {
        return this.output;
    }

    public void connect() throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void doNick(String nick)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void doQuit()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void doJoin(String room)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isConnected()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void print(String msg)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void print(String target, String msg)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTimeout(int millis)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEncoding(String encoding)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBotLogger(BotLogger logger)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BotLogger getBotLogger()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getHost()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNick()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
