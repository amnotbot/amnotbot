package org.knix.amnotbot.cmd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.knix.amnotbot.BotLogger;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotDBFactory
{
    String backend, driver;
    static BotDBFactory _instance = null;

    public static BotDBFactory instance()
    {
        if (_instance == null) {
            _instance = new BotDBFactory();
        }
        return _instance;
    }

    protected BotDBFactory()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            BotLogger.getDebugLogger().debug(e);
        }
        
        this.backend = BotConfiguration.getConfig().getString("backend");
        if (this.backend.equals("hsqldb")) {
            this.driver = this.backend + ":file";           
        } else {
            this.driver = this.backend;
        }
    }

    public Connection getConnection(String db) throws SQLException
    {
        Connection connection;

        connection = DriverManager.getConnection(
                    "jdbc:" + this.driver + ":" + db, "sa", "");

        return connection;
    }

    public QuoteDAO createQuoteDAO(String db)           
    {
        QuoteDAO result = null;
        Class quoteClass = null;
        
        try {            
            String className = this.backend.substring(0, 1).toUpperCase() +
                    this.backend.substring(1);
            quoteClass = Class.forName("org.knix.amnotbot.cmd.db." +
                    className + "QuoteDAO");

            Class[] types = { java.sql.Connection.class };      
            java.lang.reflect.Constructor constructor =
                    quoteClass.getConstructor(types);

            Object[] params = { this.getConnection(db) };          
            result = (QuoteDAO) constructor.newInstance( params );
            
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e);
        }

        return result;
    }

    public WordCounterDAO createWordCounterDAO(String db)           
    {
        WordCounterDAO wCounterDAO = null;

        try {
            return (WordCounterDAO) new JDBCWordCounterDAO(db);
        } catch (SQLException e) {
            BotLogger.getDebugLogger().debug(e);
        }

        return wCounterDAO;
    }
}
