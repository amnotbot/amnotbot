package java.org.knix.amnotbot;

import java.util.List;
import java.util.TimerTask;
import org.knix.amnotbot.BotConnection;

/**
 *
 * @author gpoppino
 */
public class BotTask extends TimerTask
{
    int period;
    BotConnection conn;
    List<String> channels;

    BotTask()
    {
        this.period = 10;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void setChannels(List<String> channels)
    {
        this.channels = channels;
    }
}
