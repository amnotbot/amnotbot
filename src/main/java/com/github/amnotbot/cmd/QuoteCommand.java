package com.github.amnotbot.cmd;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;

public class QuoteCommand implements BotCommand {

    @Override
    public void execute(BotMessage message) {
        QuoteCommandImp quoteCommandImp = new QuoteCommandImp();

        if (message.getParams() == null || message.getParams().isEmpty()) {
            quoteCommandImp.showRandomQuote(message);
        } else {
            quoteCommandImp.addQuote(message);
        }
    }

    @Override
    public String help() {
        return "";
    }

}
