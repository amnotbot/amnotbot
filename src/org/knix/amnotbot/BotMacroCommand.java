package org.knix.amnotbot;

import java.util.Iterator;
import java.util.LinkedList;

import org.schwering.irc.lib.IRCUser;

public class BotMacroCommand extends BotCommandImp
{

    private LinkedList<BotCommandInterface> commands;
    private LinkedList<BotCommandInterface> receivers;

    public BotMacroCommand()
    {
        super(null, null);

        this.commands = new LinkedList<BotCommandInterface>();
        this.receivers = new LinkedList<BotCommandInterface>();
    }

    public void add(BotCommandInterface command)
    {
        BotLogger.getDebugLogger().debug(command.getClass().getName());
        this.commands.add(command);
    }

    public void remove(BotCommandInterface command)
    {
        this.commands.remove(command);
    }

    public LinkedList<BotCommandInterface> getCommands()
    {
        return this.commands;
    }

    public boolean matches(String msg)
    {
        this.receivers.clear();
        
        boolean match = false;
        Iterator<BotCommandInterface> it = this.commands.iterator();
        while (it.hasNext()) {
            BotCommandInterface command = it.next();

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
        Iterator<BotCommandInterface> it = this.receivers.iterator();
        while (it.hasNext()) {
            BotCommandInterface command = it.next();

            command.execute(con, chan, user, msg);
        }
        this.receivers.clear();
    }
}
