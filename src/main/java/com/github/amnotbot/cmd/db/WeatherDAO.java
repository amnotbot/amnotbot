package com.github.amnotbot.cmd.db;

import java.sql.SQLException;

/**
 *
 * @author gpoppino
 */
public interface WeatherDAO
{

    public String getStation(String network, String user)
            throws SQLException;

    public void setStation(String network, String user, String station)
            throws SQLException;

    public void createStationDB() throws SQLException;

    public boolean stationDBExists() throws SQLException;

}
