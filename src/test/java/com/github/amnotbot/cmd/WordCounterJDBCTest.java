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
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.amnotbot.cmd.db.BotDBFactory;
import com.github.amnotbot.cmd.db.backend.JDBCWordCounterDAO;

import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class WordCounterJDBCTest
{
 
    private String topLinesResult;
    private final String mostUsedWord = "alto";
    private final String topLines_nick = "knix";
    private final String mostUsedWord_nick = "gresco";
    private static final String dbFilename = "unittest.db";
    private final int topLines_1 = 100, topLines_2 = 12;

    public WordCounterJDBCTest() throws SQLException
    {
        int top = this.topLines_1 + this.topLines_2;
        this.topLinesResult = this.topLines_nick +
                "(" + Integer.toString(top) + ")";        
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        File dir = new File(".");
        String[] list = dir.list();
        for (String f : list) {
            if (f.startsWith(dbFilename)) {
                File tmpFile = new File(f);
                tmpFile.delete();
            }
        }
    }

    @Before
    public void setUp()
    {
        this.initDB();
    }

    public void initDB()
    {
        Statement statement;
        Connection connection;
        try {
            connection = BotDBFactory.instance().getConnection(dbFilename);
            statement = connection.createStatement();
            
            statement.executeUpdate("CREATE TABLE words " +
                    "(d DATE, nick VARCHAR(50), word VARCHAR(50), repetitions REAL)");           
            statement.executeUpdate("CREATE TABLE lines " +
                    "(d DATE, nick VARCHAR(50), repetitions REAL)");
            
            statement.executeUpdate("CREATE UNIQUE INDEX dnw ON words" +
                    " (d, nick, word)");            
            statement.executeUpdate("CREATE INDEX n ON words (nick)");
            statement.executeUpdate("CREATE UNIQUE INDEX l ON lines (d, nick)");
            statement.executeUpdate("CREATE INDEX ln ON lines (nick)");          
        } catch (SQLException e) {
            System.err.println(e);
            return;
        }
        try {
            statement.executeUpdate("INSERT INTO words " +
                    "values('2009-06-14','gresco'," + "'" +
                    this.mostUsedWord + "'" + ", 15.0)");           
            statement.executeUpdate("INSERT INTO words " +
                    "values('2009-06-14','gresco','medio',7)");
            statement.executeUpdate("INSERT INTO words " +
                    "values('2009-06-14','knix','bajo', 5)");
            statement.executeUpdate("INSERT INTO words " +
                    "values('2009-06-14','knix','medio', 4)");
            statement.executeUpdate("INSERT INTO lines " +
                    "values('2009-06-14','gresco',94)");
            statement.executeUpdate("INSERT INTO lines " +
                    "values('2009-06-13','" + this.topLines_nick
                    + "'," + this.topLines_1 + ")");
            statement.executeUpdate("INSERT INTO lines " +
                    "values('2009-06-14','" + this.topLines_nick
                    + "'," + this.topLines_2 + ")");

            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    @After
    public void tearDown()
    {
        this.closeDB();
    }

    public void closeDB()
    {
        Statement statement;
        try {
            Connection connection =
                    BotDBFactory.instance().getConnection(dbFilename);

            statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS lines");
            statement.executeUpdate("DROP TABLE IF EXISTS words");
            
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e);
        }       
    }

    @Test
    public void testMostUsedWords() throws SQLException
    {
        System.out.println("mostUsedWords");
        String[] nicks = new String[] { null };
        JDBCWordCounterDAO wCounter = new JDBCWordCounterDAO(dbFilename);
        String result = wCounter.mostUsedWords(1, nicks, null);
        assertTrue(result.startsWith(this.mostUsedWord));
    }

    @Test
    public void testMostUsedWordsBy() throws SQLException
    {
        System.out.println("mostUsedWordsBy");
        String[] words = new String[] { "medio" };
        JDBCWordCounterDAO wCounter = new JDBCWordCounterDAO(dbFilename);
        String result = wCounter.mostUsedWordsBy(1, words, null);        
        assertTrue(result.startsWith(this.mostUsedWord_nick));
    }

    @Test
    public void testTopLines() throws SQLException
    {
        System.out.println("topLines");
        JDBCWordCounterDAO wCounter = new JDBCWordCounterDAO(dbFilename);
        String result = wCounter.topLines(1, null);        
        assertTrue(result.startsWith(this.topLinesResult));  
    }

    @Test
    public void testAvgWordsLine() throws SQLException
    {
        System.out.println("avgWordsLine");
        String[] nicks = new String[] { "gresco", "knix" };
        JDBCWordCounterDAO wCounter = new JDBCWordCounterDAO(dbFilename);
        String result = wCounter.avgWordsLine(1, nicks, null);       
        assertEquals("gresco(0.23)", result);
    }
    
    @Test
    public void testCountUniqueWords() throws SQLException
    {
        System.out.println("countUniqueWords");
        String[] nicks = new String[] { "gresco", "knix" };
        JDBCWordCounterDAO wCounter = new JDBCWordCounterDAO(dbFilename);
        String result = wCounter.countUniqueWords(1, nicks, null);
        assertEquals("gresco(2)", result);
    }
}