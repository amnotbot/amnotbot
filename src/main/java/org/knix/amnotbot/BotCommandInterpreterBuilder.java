package org.knix.amnotbot;

import org.knix.amnotbot.spam.BotSpamDetector;
import org.knix.amnotbot.spam.IRCListenerSpamDetectorAdapter;

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
                new IRCListenerSpamDetectorAdapter(spamDetector, conn));
        return spamDetector;
    }
    
}
