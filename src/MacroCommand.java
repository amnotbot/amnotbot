package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

import java.util.Iterator;
import java.util.LinkedList;

public class MacroCommand extends AmnotbotCommandImp {

	private LinkedList<AmnotbotCommand> commands;
	//private AmnotbotCommand aReceiver;
	private LinkedList<AmnotbotCommand> receivers;

	public MacroCommand() {
//		this.aReceiver = null;
		super(null, null);

		this.commands = new LinkedList<AmnotbotCommand>();
		this.receivers = new LinkedList<AmnotbotCommand>();		
	}

	public void add(AmnotbotCommand command)
	{
		System.out.println("Class = " + command.getClass().getName());
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
		
		Iterator<AmnotbotCommand> it = commands.iterator();
		boolean match = false;
		while (it.hasNext()) {
			AmnotbotCommand command = it.next();

			if (command.matches(msg)) {
				//this.aReceiver = command;
				this.receivers.add(command);
				match = true;
				//return true;
			}
		}
		
		return match;
	}

	public void execute(BotConnection con, String chan, IRCUser user, String msg)
	{
		//if (this.aReceiver != null)
		//	this.aReceiver.execute(con, chan, user, msg);
		
		Iterator<AmnotbotCommand> it = this.receivers.iterator();
		while (it.hasNext()) {
			AmnotbotCommand command = it.next();
			
			command.execute(con, chan, user, msg);						
		}
		this.receivers.clear();
	}
}
