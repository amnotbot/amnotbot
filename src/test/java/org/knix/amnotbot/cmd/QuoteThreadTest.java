package org.knix.amnotbot.cmd;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.BotTestFactory;
import org.knix.amnotbot.DummyConnection;
import org.knix.amnotbot.cmd.db.BotDBFactory;
import org.knix.amnotbot.cmd.db.TableOperations;
import org.knix.amnotbot.config.BotConfiguration;
import org.schwering.irc.lib.IRCUser;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class QuoteThreadTest
{
    private String trigger;
    private BotTestFactory factory;    
    private TableOperations tableOperations;
    private static final String dbFilename = "unitquotes.db";

    public QuoteThreadTest()
    {
        this.factory = new BotTestFactory();
        this.tableOperations = this.factory.createTableOperationsObject();
        this.trigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
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
    public void setUp() throws SQLException
    {        
        Connection connection =
                    BotDBFactory.instance().getConnection(dbFilename);
                        
        this.tableOperations.createTable(connection);

        connection.close();
        connection = null;
    }

    @After
    public void tearDown() throws SQLException
    {
        Connection connection =
                    BotDBFactory.instance().getConnection(dbFilename);

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
        IRCUser user = new IRCUser("gresco", "Geronimo", "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user, this.trigger + "quote");

        new QuoteThread(dbFilename, msg).join();

        assertTrue(conn.getOutput() != null);        
    }
    
    @Test
    public void testCreateQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testCreateQuote");
        BotMessage msg;
        String text = "Everybody is free";
        DummyConnection conn = new DummyConnection();
        IRCUser user = new IRCUser("gresco", "Geronimo", "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user,
                this.trigger + "quote op:set text:" + text);

        new QuoteThread(dbFilename, msg).join();

        assertTrue(conn.getOutput().contains("success"));        
    }

    @Test
    public void testDeleteQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testDeleteQuote");
        BotMessage msg;
        DummyConnection conn = new DummyConnection();
        IRCUser user = new IRCUser("gresco", "Geronimo", "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user,
                this.trigger + "quote op:del id:1");

        new QuoteThread(dbFilename, msg).join();

        assertTrue(conn.getOutput().contains("success"));
    }

    @Test
    public void testGetInfoQuote() throws InterruptedException
    {
        System.out.println("QuoteThreadTest : testGetInfoQuote");
        BotMessage msg;
        DummyConnection conn = new DummyConnection();
        IRCUser user = new IRCUser("gresco", "Geronimo", "localhost@mydomain");
        msg = new BotMessage(conn, "#chan", user,
                this.trigger + "quote op:info id:1");

        new QuoteThread(dbFilename, msg).join();

        assertTrue(conn.getOutput() != null);        
    }
}