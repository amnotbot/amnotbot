/*
 * Copyright (c) 2007 Jimmy Mitchener <jimmy.mitchener@gmail.com>
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

import static org.knix.amnotbot.BotLogger.BOT_HOME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Magic eightball thread.
 * Takes a question and prints out a magical response!
 * 
 * @author Jimmy Mitchener
 * @version 0.01
 */
public class EightBall extends Thread
{
	private BotConnection con;

	private String chan;
	private String nick;

	private static long timestampBuf = 0;

	private static List<String> ballResponses;

	static {
		try {
			loadResponseList();
		} catch (IOException e) {
			throw new RuntimeException(
					"Failed to load Eightball response list", e);
		}
	}

	/**
	 * Create new EightBall thread.
	 * 
	 * @param con Connection to print response.
	 * @param chan Channel to print to.
	 * @param nick User that asked the question.
	 */
	public EightBall(BotConnection con, String chan, String nick)
	{
		this.con = con;
		this.chan = chan;
		this.nick = nick;

		start();
	}

	public void run()
	{
		try {
			con.doPrivmsg(chan, nick + ": " + getMessage());
		} catch (IOException e) {
			e.printStackTrace(con.getBotLogger().getDefaultLogger());
		}
	}

	private static String getMessage()
	throws IOException
	{
		loadResponseList();

		int rand = (int) (Math.random() * ballResponses.size());
		return ballResponses.get(rand);
	}

	private static void loadResponseList()
	throws IOException
	{
		try {
			File fd = new File(BOT_HOME, "data" + File.separator +
			"eightball");

			if (fd.lastModified() > timestampBuf) {
				// FIXME - we cannot currently log this because it's static
				System.err.println(BotConstants.getBotConstants().getAppPFX() + " Reloading eightball data");

				timestampBuf = fd.lastModified();
				BufferedReader reader = new BufferedReader(new FileReader(fd));
				String buf;

				ballResponses = new ArrayList<String>();

				while ((buf = reader.readLine()) != null) {
					ballResponses.add(buf);
				}

				ballResponses = Collections.unmodifiableList(ballResponses);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to get eightball data", e);
		}
	}
}
