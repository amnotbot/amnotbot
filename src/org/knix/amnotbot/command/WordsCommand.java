package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;

public class WordsCommand extends BotCommandImp
{

    public WordsCommand()
    {
        super("^!(w(ords)?)\\s?(.*)", "w words");
    }

    public void execute(BotConnection con, String chan, IRCUser user, String m)
    {
        new WordsCommandThread(con, chan, user, this.getGroup(3),
                WordsCommandThread.countOperation.WORDS);
    }

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