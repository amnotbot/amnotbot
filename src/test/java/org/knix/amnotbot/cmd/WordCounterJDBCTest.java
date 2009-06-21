package org.knix.amnotbot.cmd;

import org.knix.amnotbot.cmd.db.JDBCWordCounterDAO;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knix.amnotbot.cmd.db.BotDBFactory;
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
                    "(word VARCHAR, repetitions REAL)");    
            statement.executeUpdate("CREATE TABLE datewordsnick " +
                    "(d DATE, nick VARCHAR, word VARCHAR, repetitions REAL)");           
            statement.executeUpdate("CREATE TABLE lines " +
                    "(d DATE, nick VARCHAR, repetitions REAL)");
            
            statement.executeUpdate("CREATE UNIQUE INDEX dnw ON datewordsnick" +
                    " (d, nick, word)");            
            statement.executeUpdate("CREATE INDEX n ON datewordsnick (nick)");
            statement.executeUpdate("CREATE UNIQUE INDEX w ON words (word)");
            statement.executeUpdate("CREATE UNIQUE INDEX l ON lines (d, nick)");
            statement.executeUpdate("CREATE INDEX ln ON lines (nick)");          
        } catch (SQLException e) {
            System.err.println(e);
            return;
        }
        try {
            statement.executeUpdate("INSERT INTO datewordsnick " +
                    "values('2009-06-14','gresco'," + "'" +
                    this.mostUsedWord + "'" + ", 15.0)");           
            statement.executeUpdate("INSERT INTO datewordsnick " +
                    "values('2009-06-14','gresco','medio',7)");
            statement.executeUpdate("INSERT INTO datewordsnick " +
                    "values('2009-06-14','knix','bajo', 5)");
            statement.executeUpdate("INSERT INTO datewordsnick " +
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
            statement.executeUpdate("DROP TABLE IF EXISTS datewordsnick");
            
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
}