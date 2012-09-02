package com.github.amnotbot.task.stats;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Encapsulates all the statistics tables available and their operations.
 * Handles table creation, dropping and update on every table. To perform any
 * operation on statistic tables, the table manager should be used
 * instead of the table object directly.
 * @author gpoppino
 */
public class StatsTableManager
{
    private String backend;
    private Connection conn = null;
    private LinkedList<StatsTableDAO> tables;

    /**
     * Creates a new table manager.
     * @param backend Database type to use for stats: hsqldb or sqlite.
     */
    public StatsTableManager(String backend)
    {
        this.backend = backend;
        this.tables = StatsFactory.instance().getTables(backend);
    }

    /**
     * Drops and creates all the tables handled by the table manager.
     * It can be thought as a "refresh" operation on the tables.
     * @param db Database that contains the tables handled by the table manager.
     * @throws SQLException
     */
    public void init(String db) throws SQLException
    {
        this.conn = StatsFactory.instance().getConnection(this.backend, db);

        this.drop(this.conn);
        this.create(this.conn);
    }

    /**
     * Closes the database connection held by the Table Manager.
     * @throws SQLException
     */
    public void close() throws SQLException
    {
        if (!this.conn.getAutoCommit()) {
            this.conn.commit();
        }
        
        StatsFactory.instance().closeConnection(this.backend, this.conn);
        
        this.conn = null;
    }

    /**
     * Executes an update on every table handled by the Table Manager.
     * @param statsEntity
     * @throws SQLException
     */
    public void update(StatsRecordDAO statsEntity)
            throws SQLException
    {
        for (StatsTableDAO t : this.tables) {
            t.update(this.conn, statsEntity);
        }
    }

    private void create(Connection conn) throws SQLException
    {
        for (StatsTableDAO t : this.tables) {
            t.create(conn);
        }
    }

    private void drop(Connection conn) throws SQLException
    {
        for (StatsTableDAO t : this.tables) {
            t.drop(conn);
        }
    }
}
