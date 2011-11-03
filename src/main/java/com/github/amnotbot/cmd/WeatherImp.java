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
package com.github.amnotbot.cmd;

import java.sql.SQLException;
import java.util.Iterator;
import net.sf.jweather.Weather;
import net.sf.jweather.metar.Metar;
import net.sf.jweather.metar.SkyCondition;
import net.sf.jweather.metar.WeatherCondition;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.db.BotDBFactory;
import com.github.amnotbot.cmd.db.WeatherDAO;

/**
 *
 * @author gpoppino
 */
class WeatherImp
{
    private final BotMessage msg;
    private final String station;

    public WeatherImp(BotMessage msg)
    {
        this.msg = msg;
        this.station = msg.getText().toUpperCase().trim();
    }

    public void run()
    {
        try {
            if (this.station.isEmpty()) {
                this.showDefaultStation();
            } else if (this.station.startsWith("*")) {
                this.setAndShowDefaultStation();
            } else {
                this.showStation(this.station);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
    }

    private void showDefaultStation() throws SQLException
    {
        WeatherDAO wDAO;
        wDAO = BotDBFactory.instance().createWeatherDAO();

        String _station = wDAO.getStation(this.msg.getConn().getHost(),
                this.msg.getUser().getNick());

        this.showStation(_station);
    }

    private void setAndShowDefaultStation() throws SQLException
    {
        String _station = this.station.substring(1);
        final Metar metar = Weather.getMetar(_station);
        if (metar == null) {
            this.showHelp();
            return;
        }
        WeatherDAO wDAO = BotDBFactory.instance().createWeatherDAO();
        wDAO.setStation(this.msg.getConn().getHost(),
                this.msg.getUser().getNick(), _station);

        this.showStation(metar);
    }

    private void showStation(String _station)
    {
        if (_station == null) {
            this.showHelp();
            return;
        }
        
        final Metar metar = Weather.getMetar(_station);
        if (metar == null) {
            this.showHelp();
            return;
        }
        this.showStation(metar);
    }

    private void showStation(Metar metar)
    {
        this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                this.getReport(metar));
    }

    private void showHelp()
    {
        this.msg.getConn().doPrivmsg(this.msg.getTarget(), "Station IDs: " +
                    "http://www.weather.gov/tg/siteloc.shtml");
    }

    private String getReport(Metar metar)
    {
        String report = new String();

        report += metar.getStationID();
        report += " Temperature: " + metar.getTemperatureInCelsius() + "°C ";
        report += metar.getTemperatureInFahrenheit() + "°F";
        
        if (metar.getPressure() != null) {
            report += ", Pressure: " + metar.getPressure() + " hPa";
        }

        String wind = new String();
        if (metar.getWindDirection() != null) {
            wind += " " + metar.getWindDirection() + "°";
        }
        if (metar.getWindSpeedInMPH() != null) {
            wind += " at " + metar.getWindSpeedInMPH() + " MPH";
        }
        if (!wind.isEmpty()) {
            report += ", Wind:" + wind;
        }

        Iterator it = metar.getSkyConditions().iterator();
        if (it.hasNext()) {
            report += ", Sky Conditions:";
        }
        while (it.hasNext()) {
            SkyCondition s = (SkyCondition) it.next();
            report += " " + s.getNaturalLanguageString();
        }

        it = metar.getWeatherConditions().iterator();
        if (it.hasNext()) {
            report += ", Weather Conditions:";
        }
        while (it.hasNext()) {
            WeatherCondition w = (WeatherCondition) it.next();
            report += " " + w.getNaturalLanguageString();
        }

        return report;
    }

}
