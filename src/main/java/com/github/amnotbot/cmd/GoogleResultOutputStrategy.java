package com.github.amnotbot.cmd;

import com.github.amnotbot.BotMessage;

/**
 *
 * @author gpoppino
 */
public interface GoogleResultOutputStrategy
{

    public void showAnswer(BotMessage msg, GoogleResult result)
            throws Exception;
}
