package org.knix.amnotbot;

public class BotShutdownHandler extends Thread {
    
    private IBot bot;
    
    public BotShutdownHandler(IBot bot) {
        this.bot = bot;
    }
    
    @Override
    public void run()
    {
        BotLogger.getDebugLogger().debug("Shutting down ...");
        
        this.bot.shutdown();
    }

}
