package com.github.amnotbot.task.stats.backends;

import com.github.amnotbot.task.stats.StatsRecordDAO;
import com.github.amnotbot.task.stats.StatsTableDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gpoppino
 */
public class HsqldbLinesTableDAO implements StatsTableDAO
{

    @Override
    public void create(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();

        statement.executeUpdate("CREATE TABLE lines " +
                "(d DATE, nick VARCHAR(50), repetitions REAL)");

        statement.executeUpdate("CREATE UNIQUE INDEX ldn ON lines (d, nick)");
        statement.executeUpdate("CREATE INDEX ln ON lines (nick)");

        statement.close();
    }

    @Override
    public void drop(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();

        statement.executeUpdate("DROP TABLE IF EXISTS lines");

        statement.close();
    }

    @Override
    public void update(Connection conn, StatsRecordDAO r)
            throws SQLException
    {
        if (r.getWord() != null) return;

        PreparedStatement updateLines = conn.prepareStatement(
                "UPDATE lines SET d = ?, nick = ?, " +
                "repetitions = (repetitions+1) WHERE d = ? AND nick = ?");

        updateLines.setDate(1, new java.sql.Date(r.getDate().getTime()));
        updateLines.setString(2, r.getNick());
        updateLines.setDate(3, new java.sql.Date(r.getDate().getTime()));
        updateLines.setString(4, r.getNick());

        int i = updateLines.executeUpdate();
        updateLines.close();

        if (i == 0) {
            PreparedStatement insertLines = conn.prepareStatement(
                    "INSERT INTO lines values(?, ?, 1)");

            insertLines.setDate(1, new java.sql.Date(r.getDate().getTime()));
            insertLines.setString(2, r.getNick());

            insertLines.executeUpdate();
            insertLines.close();
        }
    }
}
