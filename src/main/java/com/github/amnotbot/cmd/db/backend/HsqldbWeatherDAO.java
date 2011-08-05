package com.github.amnotbot.cmd.db.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.amnotbot.cmd.db.BotDBFactory;
import com.github.amnotbot.cmd.db.WeatherDAO;

/**
 *
 * @author gpoppino
 */
public class HsqldbWeatherDAO implements WeatherDAO
{
    final private String db;

    public HsqldbWeatherDAO(String db)
    {
        this.db = db;        
    }

    public String getStation(String network, String user)
            throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        PreparedStatement smt = c.prepareStatement(
                "SELECT station FROM weather WHERE network = ? AND user = ?");
        smt.setString(1, network);
        smt.setString(2, user);

        String station = null;
        ResultSet rs = smt.executeQuery();
        if (rs.next()) {
            station = rs.getString(1);
        }
        rs.close();
        smt.close();
        c.close();
        
        return station;
    }

    public void setStation(String network, String user, String station)
            throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        PreparedStatement updateSmt = c.prepareStatement(
                "UPDATE weather SET station = ?" +
                " WHERE network = ? AND user = ?");
        updateSmt.setString(1, station);
        updateSmt.setString(2, network);
        updateSmt.setString(3, user);

        int ret = updateSmt.executeUpdate();
        updateSmt.close();
        if (ret == 0) {
            PreparedStatement insertSmt = c.prepareStatement(
                    "INSERT INTO weather values(?,?,?)");
            insertSmt.setString(1, user);
            insertSmt.setString(2, network);
            insertSmt.setString(3, station);

            insertSmt.executeUpdate();
            insertSmt.close();
        }
        c.close();
    }

    public void createStationDB() throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        Statement smt = c.createStatement();

        smt.executeUpdate("CREATE TABLE weather " +
               "(user VARCHAR(50), network VARCHAR(255), station VARCHAR(50))");
        smt.executeUpdate("CREATE UNIQUE INDEX nn ON weather (user, network)");

        smt.close();
        c.close();
    }

    public boolean stationDBExists() throws SQLException
    {
        ResultSet rs = null;
        Connection c = BotDBFactory.instance().getConnection(this.db);

        rs = c.getMetaData().getTables(null, null, null,
                new String[] {"TABLE"});
 
        boolean exists = false;
        if (rs.next()) {
            exists = true;
        }
        rs.close();
        c.close();

        return exists;
    }

}
