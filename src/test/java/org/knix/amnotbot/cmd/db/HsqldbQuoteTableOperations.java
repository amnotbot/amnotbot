package org.knix.amnotbot.cmd.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gpoppino
 */
public class HsqldbQuoteTableOperations implements TableOperations
{
    @Override
    public void createTable(Connection conn) throws SQLException
    {
        Statement statement;

        statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE quotes " +
                "(id INTEGER IDENTITY, nick VARCHAR, " +
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

    @Override
    public void dropTable(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS quotes");
        statement.close();
    }
}
