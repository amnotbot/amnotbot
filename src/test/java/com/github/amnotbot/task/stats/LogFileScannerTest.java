package com.github.amnotbot.task.stats;

import com.github.amnotbot.stats.DbFileFilter;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author gpoppino
 */
@RunWith(value = Parameterized.class)
public class LogFileScannerTest
{
    final static String baseDir = "target/test-classes/";

    private static String backend;
    private static String dbFilename;
    private static DbFileFilter filter;

    public LogFileScannerTest(String _backend, String _dbFilename)
    {
        backend = _backend;
        dbFilename = _dbFilename;
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        filter = new DbFileFilter();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        File dir = new File(baseDir + "irclogs");
        String[] dlist = dir.list();
        for (String d : dlist) {
            File subdir = new File(baseDir + "irclogs" + "/" + d);
            String[] flist = subdir.list(filter);
            for (String fn : flist) {
                File f = new File(baseDir + "irclogs" + "/" + d + "/" + fn);
                f.delete();
            }
        }
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Parameters
    public static Collection testParameters()
    {
        Object[][] data = new Object[][] {
            {"sqlite", "irclogs/oftc/#amnotbot.db"},
            {"sqlite", "irclogs/freenode/#amnotbot.db"},
            {"sqlite", "irclogs/ircnet/#amnotbot.db"},
            {"hsqldb", "irclogs/oftc/#amnotbot.db"},
            {"hsqldb", "irclogs/freenode/#amnotbot.db"},
            {"hsqldb", "irclogs/ircnet/#amnotbot.db"}
        };

        return Arrays.asList(data);
    }

    @Test
    public void testScanLogs()
    {
        System.out.println("scanLogFiles: " + backend + ":" + dbFilename);
        Properties p = new Properties();

        p.setProperty("backend", backend);
        p.setProperty("ignorefile", baseDir + "ignore.words");
        p.setProperty("logdirectory", baseDir + "irclogs");
        p.setProperty("botnick", "amnotbot");
        p.setProperty("minwordlen", String.valueOf(3));
        p.setProperty("cmdtrigger", ".");

        LogFileScanner instance = new LogFileScanner(p);
        instance.scanLogFiles();
        instance = null;
    }

    @Test
    public void testQueryWords()
            throws SQLException, ClassNotFoundException
    {
        System.out.println("testQueryWords");
        String query;
        query = "SELECT word, SUM(repetitions) AS rep FROM words " +
                 "GROUP BY word ORDER BY rep DESC LIMIT 5";

        Connection conn = null;
        conn = StatsFactory.instance().getConnection(backend,
                baseDir + dbFilename);

        ResultSet rs = null;
        Statement smt = null;
        smt = conn.createStatement();        
        try {
            rs = smt.executeQuery(query);
            rs.next();
            assertEquals("developers", rs.getString(1));
            assertEquals(8, rs.getInt(2));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        rs.close();
        smt.close();
        StatsFactory.instance().closeConnection(backend, conn);
        conn = null;
    }

    @Test
    public void testQueryLines()
            throws SQLException, ClassNotFoundException
    {
        System.out.println("testQueryLines");
        String query;
        query = "SELECT nick, SUM(repetitions) AS rep FROM lines " +
                 "GROUP BY nick ORDER BY rep DESC LIMIT 5";

        Connection conn = null;
        conn = StatsFactory.instance().getConnection(backend,
                baseDir + dbFilename);

        ResultSet rs = null;
        Statement smt = null;
        smt = conn.createStatement();
        try {
            rs = smt.executeQuery(query);
            rs.next();
            assertEquals("gresco", rs.getString(1));
            assertEquals(5, rs.getInt(2));
            rs.next();
            assertEquals("knix", rs.getString(1));
            assertEquals(3, rs.getInt(2));
            assertFalse(rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        rs.close();
        smt.close();
        StatsFactory.instance().closeConnection(backend, conn);
        conn = null;
    }
}
