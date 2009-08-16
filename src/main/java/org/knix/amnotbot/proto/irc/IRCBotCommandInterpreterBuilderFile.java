package org.knix.amnotbot.proto.irc;

import org.knix.amnotbot.BotCommandInterpreterBuilderFile;
import org.knix.amnotbot.BotConnection;
import org.knix.amnotbot.spam.BotSpamDetector;
import org.knix.amnotbot.spam.IRCListenerSpamDetectorAdapter;

/**
 *
 * @author gpoppino
 */
public class IRCBotCommandInterpreterBuilderFile
        extends BotCommandInterpreterBuilderFile
{
    @Override
    public BotSpamDetector buildSpamFilter(BotConnection _conn)
    {
        IRCBotConnection conn = (IRCBotConnection)_conn;
        BotSpamDetector spamDetector = new BotSpamDetector();

        conn.addIRCEventListener(
                new IRCListenerSpamDetectorAdapter(spamDetector, conn));

        return spamDetector;
    }
}
