package org.knix.amnotbot.command.utils;

import java.util.Iterator;
import java.util.LinkedList;

public class CommandOptions {
	
	LinkedList<CmdOption> options;
	String msg;
	
	public CommandOptions(String msg) {		
		options = new LinkedList<CmdOption>();
		this.msg = msg;
	}
	
	public void addOption(CmdOption opt) 
	{
		options.add(opt);
	}
	
	public void buildArgs()
	{
		Iterator<CmdOption> it = options.iterator();
		while (it.hasNext()) {
			CmdOption opt = it.next();
			opt.buildArgs(msg);
		}
	}
	
	public CmdOption getOption(String name) 
	{
		Iterator<CmdOption> it = options.iterator();
		while (it.hasNext()) {
			CmdOption opt = it.next();
			if (opt.getName() == name)
				return opt;
		}
		return null;
	}
	
	public boolean hasOptions()
	{
		Iterator<CmdOption> it = options.iterator();
		while (it.hasNext()) {
			CmdOption opt = it.next();
			if (opt.hasValue())
				return true;
		}
		return false;
	}
	
	public int optionStartAt()
	{
		int index;
		int min = this.msg.length();
		
		Iterator<CmdOption> it = options.iterator();
		while (it.hasNext()) {
			CmdOption opt = it.next();
			index = this.msg.indexOf(opt.getName());
			
			if (index < 0)
				continue;
			
			if (index < min)
				min = index;
		}
		
		return min;
	}
}
