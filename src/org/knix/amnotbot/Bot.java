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

import java.io.IOException;
import java.util.List;

/*
 * TODO - allow for multiple servers at startup
 *      - allow user input.
 */

/**
 * Very simple IRC bot.
 * 
 * @author Jimmy Mitchener &lt;jimmy.mitchener | et | [g]mail.com&gt;
 * @version 0.01
 */
public class Bot extends Thread
{
	private String server;
	private int port;
	private List<String> channels;

	private static final int SO_TIMEOUT = 1000 * 60 * 5;

	public Bot(String server, List<String> channels)
	{
		new Bot(server, 0, channels);
	}

	/**
	 * Create a new bot.
	 */
	public Bot(String server, int port, List<String> channels)
	{
		this.server = server;
		this.port = port;
		this.channels = channels;

		start();
	}

	public void run() {
		// Should this be in the constructor ... ?
		BotLogger logger = new BotLogger(server);

		try {
			BotConnection con =
				createConnection(server, port, channels, logger);
			con.connect();

			for (;;) {
				if (!con.isConnected()) {
					try {
						con = createConnection(server, port, channels, logger);
						con.connect();
					} catch (Exception e) {
						e.printStackTrace(logger.getDefaultLogger());
					}
				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
					ie.printStackTrace(logger.getDefaultLogger());
				}
			}
		} catch (IOException e) {
			e.printStackTrace(logger.getDefaultLogger());
		}
	}

	/**
	 * Create a new bot connection.
	 * This should probably be in some kind of connection factory
	 * 
	 * @param server server we're to connect to
	 * @param channels list of channels to join on connect
	 * @return
	 */
	private static BotConnection createConnection(String server,
			int port,
			List<String> channels,
			BotLogger logger)
	{
		BotConnection con = null;

		if (port > 0)
			con = new BotConnection(server, port);
		else
			con = new BotConnection(server);

		con.setBotLogger(logger);
		con.addIRCEventListener(new BotListener(con, channels));
		con.setPong(true);
		con.setDaemon(false);
		con.setTimeout(SO_TIMEOUT);

		return con;
	}
}
