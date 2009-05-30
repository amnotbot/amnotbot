package org.knix.amnotbot.command;

import org.knix.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public interface GoogleResultOutputStrategy
{

    public void showAnswer(BotMessage msg, GoogleResult result)
            throws Exception;
}
