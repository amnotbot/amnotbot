/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.knix.amnotbot;

import org.knix.amnotbot.spam.BotSpamDetector;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterConstructor
{
    BotCommandInterpreterBuilder builder;

    public BotCommandInterpreterConstructor(BotCommandInterpreterBuilder b)
    {
        this.builder = b;
    }

    public BotCommandInterpreter construct(BotConnection conn)
    {
        BotSpamDetector spamDetector;

        spamDetector = this.builder.buildSpamFilter(conn);
        this.builder.buildInterpreter(spamDetector);
        this.builder.loadCommands();

        return this.builder.getInterpreter();
    }
    
}
