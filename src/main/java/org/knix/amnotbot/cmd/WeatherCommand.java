package org.knix.amnotbot.cmd;

import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public class WeatherCommand implements BotCommand
{

    @Override
    public void execute(BotMessage message)
    {
        WeatherImp weather = new WeatherImp(message);
        weather.run();
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
