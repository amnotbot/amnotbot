package org.knix.amnotbot;

import java.util.Iterator;
import java.util.LinkedList;

import org.schwering.irc.lib.IRCUser;

public class MacroCommand extends AmnotbotCommandImp
{

    private LinkedList<AmnotbotCommand> commands;
    private LinkedList<AmnotbotCommand> receivers;

    public MacroCommand()
    {
        super(null, null);

        this.commands = new LinkedList<AmnotbotCommand>();
        this.receivers = new LinkedList<AmnotbotCommand>();
    }

    public void add(AmnotbotCommand command)
    {
        BotLogger.getDebugLogger().debug(command.getClass().getName());
        this.commands.add(command);
    }

    public void remove(AmnotbotCommand command)
    {
        this.commands.remove(command);
    }

    public LinkedList<AmnotbotCommand> getCommands()
    {
        return this.commands;
    }

    public boolean matches(String msg)
    {
        this.receivers.clear();
        
        boolean match = false;
        Iterator<AmnotbotCommand> it = this.commands.iterator();
        while (it.hasNext()) {
            AmnotbotCommand command = it.next();

            if (command.matches(msg)) {
                this.receivers.add(command);
                match = true;
            }
        }

        return match;
    }

    public void execute(BotConnection con, String chan, IRCUser user,
            String msg)
    {
        Iterator<AmnotbotCommand> it = this.receivers.iterator();
        while (it.hasNext()) {
            AmnotbotCommand command = it.next();

            command.execute(con, chan, user, msg);
        }
        this.receivers.clear();
    }
}
