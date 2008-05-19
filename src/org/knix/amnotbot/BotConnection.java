/*
 * Copyright (c) 2008 Jimmy Mitchener <jcm@packetpan.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.knix.amnotbot;

import org.knix.amnotbot.BotConstants;

import org.schwering.irc.lib.IRCConnection;

/**
 * IRCConnection wrapper implementing some useful methods.
 */
public class BotConnection extends IRCConnection
{
	private BotLogger logger;

	private boolean silent = false;

	public BotConnection(String server,
			int[] ports,
			String passwd,
			String nick,
			String user,
			String name)
	{
		super (server, ports, passwd, nick, user, name);
	}

	public BotConnection(String server,
			int[] ports,
			String passwd,
			String nick)
	{
		super (server, ports, passwd, nick, nick, nick);
	}

	public BotConnection(String server,
			int[] ports,
			String nick)
	{
		super (server, ports, null, nick, nick, nick);
	}

	public BotConnection(String server, int port, String nick)
	{
		super (server, new int[] { port }, null, nick, nick, nick);
	}

	public BotConnection(String server, int port, String passwd, String nick)
	{
		super (server, new int[] { port }, passwd, nick, nick, nick);
	}

	public BotConnection(String server, String passwd, String nick)
	{
		super(server, BotConstants.getBotConstants().getIrcPorts(), passwd, nick, nick, nick);
	}

	public BotConnection(String server, String nick)
	{
		super (server, BotConstants.getBotConstants().getIrcPorts(), null, nick, nick, nick);
	}

	public BotConnection(String server)
	{
		super (server, 
				BotConstants.getBotConstants().getIrcPorts(), 
				null, 
				BotConstants.getBotConstants().getNick(), 
				BotConstants.getBotConstants().getNick(), 
				BotConstants.getBotConstants().getNick()
		);
	}

	public BotConnection(String server, int port)
	{
		super (server, 
				new int[] { port }, 
				null, 
				BotConstants.getBotConstants().getNick(), 
				BotConstants.getBotConstants().getNick(), 
				BotConstants.getBotConstants().getNick()
		);
	}

	public void setBotLogger(BotLogger logger)
	{
		this.logger = logger;
	}

	/**
	 * Not sure if I really want this to be available.
	 * For now it's being used by other classes to facilitate getDefaultLogger()
	 * for logging of stack traces
	 * 
	 * @return This Connection's BotLogger instance
	 */
	 public BotLogger getBotLogger()
	{
		 return logger;
	}

	/**
	 * Output messages we send to the console.
	 */
	 public void doPrivmsg(String target, String msg)
	 {
		 if (silent)
			 return;

		 super.doPrivmsg(target, msg);

		 print(target, getNick() + "> " + msg);
	 }

	 public void doNick(String nick)
	 {
		 print("doNick(" + nick + ") called.");
		 super.doNick(nick);
	 }

	 public void setSilent(boolean silent)
	 {
		 this.silent = silent;
	 }

	 public boolean isSilent()
	 {
		 return silent;
	 }

	 /**
	  * Method used to alter the bot's nick when we get a collision.
	  * This could be greatly improved.
	  */
	 public void alternateNick()
	 {
		 String nick = getNick();

		 if (nick.endsWith("_")) {
			 doNick(nick.substring(0, nick.length()-1) + "-");
		 } else {
			 doNick(nick + "_");
		 }
	 }

	 public void print(String msg)
	 {
		 System.out.println(msg);

		 if (logger != null)
			 logger.log(BotLogger.DEFAULT, msg, true);
	 }

	 public void print(String target, String msg)
	 {
		 System.out.println(target + " " + msg);

		 if (logger != null)
			 logger.log(target, msg, true);
	 }
}
