package com.github.amnotbot.task.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Allows creating connection objects for databases and obtaining
 * their table related objects.
 * @author gpoppino
 */
public class StatsFactory
{

    static StatsFactory _instance = null;

    /**
     * Obtains an instance of the StatsFactory object.
     * @return An instance of the StatsFactory object.
     */
    public static StatsFactory instance()
    {
        if (_instance == null) {
            _instance = new StatsFactory();
        }
        return _instance;
    }

    protected StatsFactory()
    {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    private String getDriver(String backend)
    {
        String driver;

        if (backend.equals("hsqldb")) {
            driver = backend + ":file";
        } else {
            driver = backend;
        }

        return driver;
    }

    /**
     * Gets a database connection for the specified backend and database file
     * name.
     * @param backend Database backend: hsqldb.
     * @param db Database file name.
     * @return A database connection object.
     * @throws SQLException
     */
    public Connection getConnection(String backend, String db)
            throws SQLException
    {
        String driver;
        Connection connection = null;

        driver = this.getDriver(backend);
        connection = DriverManager.getConnection("jdbc:" + driver + ":" + db);

        return connection;
    }

    /**
     * Closes a database connection, using the specific procedure of each
     * database to perform this task.
     * @param backend Database backend: hsqldb.
     * @param conn Database connection object.
     * @throws SQLException
     */
    public void closeConnection(String backend, Connection conn)
            throws SQLException
    {
        if (backend.equals("hsqldb")) {
            Statement smt = conn.createStatement();
            smt.execute("SHUTDOWN COMPACT");
            smt.close();
        }
        conn.close();
    }

    /**
     * Creates all statistics table objects associated with a specific backend,
     * and returns them in a linked list.
     * @param backend Database backend: hsqldb.
     * @return A list of all the statistics tables associated with the backend.
     */
    public LinkedList<StatsTableDAO> getTables(String backend)
    {       
        Class tableClass = null;
        String[] tableNames = {"Words", "Lines"};
        LinkedList<StatsTableDAO> result = new LinkedList<StatsTableDAO>();

        String className = backend.substring(0, 1).toUpperCase() +
                backend.substring(1);
        try {
            for (String t  : tableNames) {
                tableClass = Class.forName(
                        "com.github.amnotbot.task.stats.backends." + 
                        className + t + "TableDAO");
                
                result.add((StatsTableDAO) tableClass.newInstance() );
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return result;
    }
}
