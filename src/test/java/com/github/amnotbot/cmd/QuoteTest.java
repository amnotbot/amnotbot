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
package com.github.amnotbot.cmd;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.BotTestFactory;
import com.github.amnotbot.BotUser;
import com.github.amnotbot.DummyConnection;
import com.github.amnotbot.cmd.QuoteImp;
import com.github.amnotbot.cmd.db.BotDBFactory;
import com.github.amnotbot.cmd.db.TableOperations;
import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.proto.irc.IRCBotUser;

import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class QuoteTest
{
    private String trigger;
    private BotTestFactory factory;    
    private TableOperations tableOperations;
    private static final String dbFilename = "quotes.db";

    public QuoteTest()
    {
        this.factory = new BotTestFactory();
        this.tableOperations = this.factory.createTableOperationsObject();
        this.trigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
        
        File f = new File(
                SystemUtils.getUserDir().getAbsolutePath() + "/build");
        if (f.exists()) {
            System.setProperty("user.home", "build/test/");
        } else {
            System.setProperty("user.home", "test/");
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        String[] list = BotLogger.BOT_HOME.list();
        for (String f : list) {
            if (f.startsWith(dbFilename)) {
                File tmpFile = new File(BotLogger.BOT_HOME + "/" + f);
                tmpFile.delete();
            }
        }
    }

    @Before
    public void setUp() throws SQLException
    {        
        Connection connection =
                    BotDBFactory.instance().getConnection(BotLogger.BOT_HOME +
                    "/" + dbFilename);
                        
        this.tableOperations.createTable(connection);

        connection.close();
        connection = null;
        
        BotConfiguration.setHomeDir("target/.amnotbot");
    }

    @After
    public void tearDown() throws SQLException
    {
        Connection connection =
                    BotDBFactory.instance().getConnection(BotLogger.BOT_HOME +
                    "/" + dbFilename);

        this.tableOperations.dropTable(connection);
        
        connection.close();
        connection = null;
    }

    @Test
    public void testRandomQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testRandomQuote");
        BotMessage msg;
        DummyConnection conn = new DummyConnection();
        BotUser user = new IRCBotUser("gresco", "Geronimo",
                "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user, this.trigger + "quote");

        new QuoteImp(msg).run();

        assertTrue(conn.getOutput() != null);        
    }
    
    @Test
    public void testCreateQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testCreateQuote");
        BotMessage msg;
        String text = "Everybody is free";
        DummyConnection conn = new DummyConnection();
        BotUser user = new IRCBotUser("gresco", "Geronimo",
                "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user,
                this.trigger + "quote op:set text:" + text);

        new QuoteImp(msg).run();

        assertTrue(conn.getOutput().contains("success"));        
    }

    @Test
    public void testDeleteQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testDeleteQuote");
        BotMessage msg;
        DummyConnection conn = new DummyConnection();
        BotUser user = new IRCBotUser("gresco", "Geronimo",
                "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user,
                this.trigger + "quote op:del id:1");

        new QuoteImp(msg).run();

        assertTrue(conn.getOutput().contains("success"));
    }

    @Test
    public void testGetInfoQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testGetInfoQuote");
        BotMessage msg;
        DummyConnection conn = new DummyConnection();
        BotUser user = new IRCBotUser("gresco", "Geronimo",
                "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user,
                this.trigger + "quote op:info id:1");

        new QuoteImp(msg).run();

        assertTrue(conn.getOutput() != null);        
    }
}