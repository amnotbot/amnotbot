package com.github.amnotbot;

import com.github.amnotbot.spam.BotSpamDetector;

/**
 *
 * @author gpoppino
 */
public abstract class BotCommandInterpreterBuilder
{

    public abstract void buildInterpreter(BotSpamDetector spamDetector);

    public abstract BotCommandInterpreter getInterpreter();

    public abstract void loadCommands();

    public abstract BotSpamDetector buildSpamFilter(BotConnection conn);
    
}
