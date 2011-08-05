package com.github.amnotbot.proto.irc;


import com.github.amnotbot.BotCommandInterpreterBuilderFile;
import com.github.amnotbot.BotConnection;
import com.github.amnotbot.spam.BotSpamDetector;
import com.github.amnotbot.spam.IRCListenerSpamDetectorAdapter;

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
