package org.knix.amnotbot;

/**
 *
 * @author gpoppino
 */
public class DummyConnection extends BotConnection
{
    private String output;

    public DummyConnection()
    {
        super("localhost", new int[]{6667}, null, null, null, null);

        this.output = null;
    }

    public void doPrivmsg(String target, String msg)
    {
        this.output = msg;
    }

    public String getOutput()
    {
        return this.output;
    }

}
