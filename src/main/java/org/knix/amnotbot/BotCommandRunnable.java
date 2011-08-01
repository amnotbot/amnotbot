package org.knix.amnotbot;

/**
 *
 * @author gpoppino
 */
public class BotCommandRunnable implements Runnable
{
    private final BotCommand cmd;
    private final BotMessage msg;

    BotCommandRunnable(BotCommand cmd, BotMessage msg)
    {
        this.cmd = cmd;
        this.msg = msg;
    }

    public void run()
    {
        this.cmd.execute(this.msg);
    }

}
