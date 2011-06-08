package org.knix.amnotbot;

import java.util.List;
import java.util.TimerTask;

/**
 *
 * @author gpoppino
 */
public abstract class BotTask extends TimerTask
{
    int period;
    BotConnection conn;
    List<String> channels;

    public BotTask()
    {
        this.period = 10;
    }

    public long getPeriod()
    {
        return this.period * 1000 * 60;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }

    public void setConnection(BotConnection conn)
    {
        this.conn = conn;
    }

    public BotConnection getConnection()
    {
        return this.conn;
    }

    public void setChannels(List<String> channels)
    {
        this.channels = channels;
    }

    public List<String> getChannels()
    {
        return this.channels;
    }

}
