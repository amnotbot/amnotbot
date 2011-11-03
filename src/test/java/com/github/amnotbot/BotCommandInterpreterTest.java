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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotCommandEvent;
import com.github.amnotbot.BotCommandInterpreter;
import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.BotUser;
import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.proto.irc.IRCBotUser;
import com.github.amnotbot.spam.BotSpamDetector;

import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterTest
{

    private SharedObject s;
    private BotConnection conn;

    public BotCommandInterpreterTest()
    {
    }

    @Before
    public void setUp()
    {
        this.s = new SharedObject();
        this.conn = new DummyConnection();
        BotConfiguration.setHomeDir("target/.amnotbot");
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testAddListener() throws InterruptedException
    {
        System.out.println("addListener");
        BotUser user = new IRCBotUser("gresco1", "geronimo1", "localhost");
        String trigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
        BotMessage msg = new BotMessage(this.conn, "#chan", user, 
                trigger + "a testA");
        BotCommandInterpreter instance =
                new BotCommandInterpreter(new BotSpamDetector());

        instance.addListener(new BotCommandEvent("a"), new CommandA(this.s));
        instance.addListener(new BotCommandEvent("b"), new CommandB(this.s));

        this.s.setValue("D");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("A", this.s.getValue());

        msg.setText(trigger + "b testB");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("B", this.s.getValue());

        this.s.setValue("C");
        msg.setText(trigger + "g");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("C", this.s.getValue());

        msg.setText(trigger + "a testA");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("A", this.s.getValue());
    }

    @Test
    public void testAddLinkListener() throws InterruptedException
    {
        System.out.println("addLinkListener");   
        BotCommandInterpreter instance = 
                new BotCommandInterpreter(new BotSpamDetector());
        BotUser user = new IRCBotUser("gresco", "geronimo", "localhost");
        BotMessage msg = new BotMessage(this.conn, "#chan", user,
                "http://www.abc.com");
        
        instance.addLinkListener(new CommandLink(this.s));

        this.s.setValue("D");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("Link", this.s.getValue());
    }

    @Test
    public void testHelp() throws InterruptedException
    {
        System.out.println("testHelp");
        BotUser user = new IRCBotUser("gresco1", "geronimo1", "localhost");
        String trigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
        BotMessage msg = new BotMessage(this.conn, "#chan", user,
                trigger + trigger + "a");
        BotCommandInterpreter instance =
                new BotCommandInterpreter(new BotSpamDetector());

        instance.addListener(new BotCommandEvent("a"), new CommandA(this.s));
        instance.addListener(new BotCommandEvent("b"), new CommandB(this.s));
        instance.addLinkListener(new CommandLink(this.s));

        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpA", this.s.getValue());

        msg.setText(trigger + trigger + "b");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpB", this.s.getValue());

        this.s.setValue("C");
        msg.setText(trigger + trigger + "g");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("C", this.s.getValue());

        msg.setText(trigger + trigger + "a testA");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpA", this.s.getValue());

        this.s.setValue("D");
        msg.setText(trigger + trigger + "url");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpLink", this.s.getValue());
    }
    
    class SharedObject 
    {
        String value;
        
        SharedObject() 
        {
            this.value = null;
        }
        
        public void setValue(String val)
        {
            this.value = val;
        }
        
        public String getValue()
        {
            return this.value;
        }
    }

    class CommandA implements BotCommand
    {
        SharedObject a;

        public CommandA(SharedObject a)
        {
            this.a = a;
        }

        public void execute(BotMessage msg)
        {
            this.a.setValue("A");
            System.out.println("A:" + msg.getText());
        }

        public String help()
        {
            String h = "HelpA";
            this.a.setValue(h);
            System.out.println("Help: " + h);
            return h;
        }
    }

    class CommandB implements BotCommand
    {
        SharedObject b;

        public CommandB(SharedObject a)
        {
            this.b = a;
        }

        public void execute(BotMessage msg)
        {
            this.b.setValue("B");
            System.out.println("B:" + msg.getText());
        }

        public String help() 
        {
            String h = "HelpB";
            this.b.setValue(h);
            System.out.println("Help: " + h);
            return h;
        }
    }

    class CommandLink implements BotCommand
    {
        SharedObject l;

        public CommandLink(SharedObject a)
        {
            this.l = a;
        }

        public void execute(BotMessage msg)
        {
            this.l.setValue("Link");
            System.out.println("Link: " + msg.getText());
        }

        public String help() 
        {
            String h = "HelpLink";
            this.l.setValue(h);
            System.out.println("Help: " + h);
            return h;
        }
    }
}