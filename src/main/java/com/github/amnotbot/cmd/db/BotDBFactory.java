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
package com.github.amnotbot.cmd.db;

import com.github.amnotbot.cmd.db.backend.JDBCWordCounterDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotDBFactory
{
    String backend, driver;
    Properties properties;
    static BotDBFactory _instance = null;
    QuoteDAO quoteDAO = null;
    WeatherDAO weatherDAO = null;

    public static BotDBFactory instance()
    {
        if (_instance == null) {
            _instance = new BotDBFactory();
        }
        return _instance;
    }

    protected BotDBFactory()
    {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            BotLogger.getDebugLogger().debug(e);
        }

        this.backend = BotConfiguration.getConfig().getString("backend");

        this.properties = new Properties();
        this.driver = this.backend + ":file";
        this.properties.setProperty("shutdown", "true");
    }

    public Connection getConnection(String db) throws SQLException
    {
        Connection connection = null;

        connection = DriverManager.getConnection(
                "jdbc:" + this.driver + ":" + db, this.properties);

        connection.setAutoCommit(true);
        return connection;
    }

    public QuoteDAO createQuoteDAO()
    {
        if (this.quoteDAO != null) return this.quoteDAO;

        Class quoteClass = null;
        String db = BotLogger.BOT_HOME + "/" + "quotes.db";
        
        try {            
            String className = this.backend.substring(0, 1).toUpperCase() +
                    this.backend.substring(1);
            quoteClass = Class.forName("com.github.amnotbot.cmd.db.backend." +
                    className + "QuoteDAO");

            Class[] types = { java.lang.String.class };
            java.lang.reflect.Constructor constructor =
                    quoteClass.getConstructor(types);

            Object[] params = { db };          
            this.quoteDAO = (QuoteDAO) constructor.newInstance( params );
            if (!this.quoteDAO.quotesDBExists()) {
                this.quoteDAO.createQuotesDB();
            }
            
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e);
        }

        return this.quoteDAO;
    }

    public WordCounterDAO createWordCounterDAO(String db)           
    {
        WordCounterDAO wCounterDAO = null;

        try {
            return (WordCounterDAO) new JDBCWordCounterDAO(db);
        } catch (SQLException e) {
            BotLogger.getDebugLogger().debug(e);
        }

        return wCounterDAO;
    }

    public WeatherDAO createWeatherDAO()
    {
        if (this.weatherDAO != null) return this.weatherDAO;

        Class weatherClass = null;
        String db = BotLogger.BOT_HOME + "/" + "weather.db";
        
        try {
            String className = this.backend.substring(0, 1).toUpperCase() +
                    this.backend.substring(1);
            weatherClass = Class.forName("com.github.amnotbot.cmd.db.backend." +
                    className + "WeatherDAO");

            Class[] types = { java.lang.String.class };
            java.lang.reflect.Constructor constructor =
                    weatherClass.getConstructor(types);
            Object[] params = { db };
            
            this.weatherDAO = (WeatherDAO) constructor.newInstance( params );
            if (!this.weatherDAO.stationDBExists()) {
                this.weatherDAO.createStationDB();
            }

        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
        return this.weatherDAO;
    }
}
