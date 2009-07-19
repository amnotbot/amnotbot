package org.knix.amnotbot.cmd.db;

import java.sql.SQLException;
import org.knix.amnotbot.BotConnection;

/**
 *
 * @author gpoppino
 */
public interface WeatherDAO
{

    public String getStation(BotConnection conn, String user)
            throws SQLException;

    public void setStation(BotConnection conn, String user, String station)
            throws SQLException;

    public void createStationDB() throws SQLException;

    public boolean stationDBExists() throws SQLException;

}
