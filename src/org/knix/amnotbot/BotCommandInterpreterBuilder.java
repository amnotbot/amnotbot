package org.knix.amnotbot;

/**
 *
 * @author gpoppino
 */
public abstract class BotCommandInterpreterBuilder
{

    public abstract void buildInterpreter(BotSpamDetector spamDetector);

    public abstract BotCommandInterpreter getInterpreter();

    public abstract void loadCommands();

    public BotSpamDetector buildSpamFilter(BotConnection conn)
    {
        BotSpamDetector spamDetector = new BotSpamDetector();
        conn.addIRCEventListener(
                new IRCListenerSpamDetectorAdapter(spamDetector)
                );
        return spamDetector;
    }
    
}
