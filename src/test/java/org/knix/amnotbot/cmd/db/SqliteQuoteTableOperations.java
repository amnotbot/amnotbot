package org.knix.amnotbot.cmd.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gpoppino
 */
public class SqliteQuoteTableOperations implements TableOperations
{

    public void createTable(Connection conn) throws SQLException
    {
        Statement statement;

        statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE quotes " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, nick VARCHAR, " +
                "desc VARCHAR)");
        statement.executeUpdate("CREATE UNIQUE INDEX id_ ON quotes (id)");
        statement.executeUpdate("CREATE INDEX nick_ ON quotes (nick)");

        statement.executeUpdate("INSERT INTO quotes (nick, desc) " +
                "values('gresco','My first quote!')");
        statement.executeUpdate("INSERT INTO quotes (nick, desc) " +
                "values('gresco','My second quote!')");
        statement.executeUpdate("INSERT INTO quotes (nick, desc) " +
                "values('knix','Hola!')");
        statement.close();
    }

    public void dropTable(Connection conn) throws SQLException
    {
         Statement statement;
         statement = conn.createStatement();
         statement.executeUpdate("DROP TABLE IF EXISTS quotes");
         statement.close();
    }
}
