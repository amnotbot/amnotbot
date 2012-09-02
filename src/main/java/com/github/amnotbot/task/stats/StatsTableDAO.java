package com.github.amnotbot.task.stats;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a table that stores statistics and its basic operations.
 * Every table class should inherit from this interface.
 * @author gpoppino
 */
public interface StatsTableDAO
{
    /**
     * This method should create the table represented by the object.
     * @param conn Database connection.
     * @throws SQLException
     */
    public void create(Connection conn) throws SQLException;
    /**
     * This method should drop the table represented by the object.
     * @param conn Database connection.
     * @throws SQLException
     */
    public void drop(Connection conn) throws SQLException;
    /**
     * This method should update the table represented by the object.
     * @param conn Database connection.
     * @param statsEntity A new record used to update the database.
     * @throws SQLException
     */
    public void update(Connection conn, StatsRecordDAO statsEntity)
            throws SQLException;
    
}
