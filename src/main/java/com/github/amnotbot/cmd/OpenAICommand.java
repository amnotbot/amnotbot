package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.config.BotConfiguration;

public class OpenAICommand implements BotCommand
{
    private final OpenAICommandImp openAI;
    public OpenAICommand()
    {
        this.openAI = new OpenAICommandImp(BotConfiguration.getConfig().getString("openai_secret_key"),
                BotConfiguration.getConfig().getString("openai_model"));
    }

    @Override
    public void execute(BotMessage message)
    {
        if (message.getParams() == null || message.getParams().isEmpty()) return;

        this.openAI.doChat(message);
    }

    @Override
    public String help()
    {
        throw new UnsupportedOperationException("Unimplemented method 'help'");
    }

}
