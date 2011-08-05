package com.github.amnotbot;

public class BotShutdownHandler extends Thread
{

    private Bot bot;

    public BotShutdownHandler(Bot bot)
    {
        this.bot = bot;
    }

    public void run()
    {
        BotLogger.getDebugLogger().debug("Shutting down ...");
        this.bot.shutdown();
    }
}
