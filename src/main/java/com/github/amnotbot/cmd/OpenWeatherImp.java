package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;
import com.github.amnotbot.config.BotConfiguration;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;
import org.schwering.irc.lib.IRCConstants;

import java.io.IOException;

public class OpenWeatherImp
{
    private final BotMessage msg;
    private String city = null;
    private String country = null;

    public OpenWeatherImp(BotMessage msg)
    {
        this.msg = msg;
        if (msg.getParams().contains(",")) {
            String args[] = msg.getText().split(",");
            if (args.length > 1) {
                this.city = args[0].trim();
                this.country = args[1].trim().isEmpty() ? null : args[1].trim();
            } else {
                this.city = args[0].trim();
            }
        } else {
            this.city = msg.getParams().trim();
        }
    }

    public void run() throws IOException
    {
        String unit = BotConfiguration.getConfig().getString("openweather_unit");

        OpenWeatherMap owm = null;
        if (unit.compareTo("metric") == 0) {
            owm = new OpenWeatherMap(OpenWeatherMap.Units.METRIC,
                    BotConfiguration.getConfig().getString("openweather_key"));
        } else {
            owm = new OpenWeatherMap(OpenWeatherMap.Units.IMPERIAL,
                    BotConfiguration.getConfig().getString("openweather_key"));
        }

        CurrentWeather cw = null;
        if (this.country == null) {
            cw = owm.currentWeatherByCityName(this.city);
        } else {
            cw = owm.currentWeatherByCityName(this.city, this.country);
        }

        if (cw.isValid()) {

            String weatherString = IRCConstants.UNDERLINE_INDICATOR + "City: " + IRCConstants.UNDERLINE_INDICATOR +
                    cw.getCityName();

            if (cw.hasWeatherInstance()) {
                if (cw.getWeatherInstance(0).hasWeatherName() && cw.getWeatherInstance(0).hasWeatherDescription()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Sky: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getWeatherInstance(0).getWeatherDescription();
                }
            }

            if (cw.hasMainInstance()) {
                String temp_unit = unit.compareTo("metric") == 0 ? " ˚C" : " ˚F";
                if (cw.getMainInstance().hasTemperature()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Current Temperature: " +
                            IRCConstants.UNDERLINE_INDICATOR + cw.getMainInstance().getTemperature() + temp_unit;
                }

                if (cw.getMainInstance().hasMaxTemperature() && cw.getMainInstance().hasMinTemperature()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Temperature Max/Min: " +
                            IRCConstants.UNDERLINE_INDICATOR + cw.getMainInstance().getMaxTemperature() +
                            "/" + cw.getMainInstance().getMinTemperature() + temp_unit;
                }

                if (cw.getMainInstance().hasHumidity()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Humidity: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getMainInstance().getHumidity() + "%";
                }

                if (cw.getMainInstance().hasPressure()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Pressure: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getMainInstance().getPressure() + " hpa";
                }
            }

            if (cw.hasRainInstance()) {
                if (cw.getRainInstance().hasRain()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Rain: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getRainInstance().getRain() + "%";
                }
            }

            if (cw.hasCloudsInstance()) {
                if (cw.getCloudsInstance().hasPercentageOfClouds()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR +
                            " Clouds: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getCloudsInstance().getPercentageOfClouds() + "%";
                }
            }

            if (cw.hasSysInstance()) {
                if (cw.getSysInstance().hasSunriseTime() && cw.getSysInstance().hasSunsetTime()) {
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Sunrise: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getSysInstance().getSunriseTime() + IRCConstants.UNDERLINE_INDICATOR + " Sunset: " +
                            IRCConstants.UNDERLINE_INDICATOR + cw.getSysInstance().getSunsetTime();
                }
            }

            if (cw.hasWindInstance()) {
                if (cw.getWindInstance().hasWindSpeed() && cw.getWindInstance().hasWindDegree()) {
                    String speed_unit = unit.compareTo("metric") == 0 ? " meter/sec" : "miles/hour";
                    weatherString += IRCConstants.UNDERLINE_INDICATOR + " Wind Speed: " + IRCConstants.UNDERLINE_INDICATOR +
                            cw.getWindInstance().getWindSpeed() + speed_unit + IRCConstants.UNDERLINE_INDICATOR +
                            " Wind Degree: " + IRCConstants.UNDERLINE_INDICATOR + cw.getWindInstance().getWindDegree();
                }
            }

            this.msg.getConn().doPrivmsg(this.msg.getTarget(), weatherString);
        }
    }
}
