/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

        smt.execute("CREATE TABLE weather " +
               "(user VARCHAR(50), network VARCHAR(255), station VARCHAR(50))");
        smt.execute("CREATE UNIQUE INDEX nn ON weather (user, network)");

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
