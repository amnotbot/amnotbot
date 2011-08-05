package com.github.amnotbot.cmd.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author gpoppino
 */
public interface TableOperations {

    public void createTable(Connection conn) throws SQLException;
    public void dropTable(Connection conn) throws SQLException;

}
