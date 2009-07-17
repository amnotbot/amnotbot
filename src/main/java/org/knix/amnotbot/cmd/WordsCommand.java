package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;

public class WordsCommand implements BotCommand
{

    public WordsCommand()
    {
    }

    @Override
    public void execute(BotMessage message)
    {
        new WordsCommandImp(message,
                WordsCommandImp.countOperation.WORDS).run();
    }

    @Override
    public String help()
    {
        String msg;

        msg = "!w or !words ";
        msg += "Options: [ nick:n1,n2,n3 ";
        msg += "word:w1,w2,w3 number:x1 ";
        msg += "op:avg ] ";
        msg += "\n Eg. !w number:10\n ";
        msg += "\n !w nick:gresco word:cheeses";

        return msg;
    }
}