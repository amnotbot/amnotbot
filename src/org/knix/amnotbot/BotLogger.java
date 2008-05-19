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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * Very basic logging mechanism designed to handle different servers/channels.
 * 
 * @author Jimmy Mitchener
 */
public class BotLogger
{
	// use Hashtable because it's synchronized
	private Map<String, PrintWriter> logs =
		new Hashtable<String, PrintWriter>();

	/** Key for the "default" logger */
	public static final String DEFAULT = ",";

	/** Thread safe SimpleDateFormat thanks to Jakarta Commons */
	private static final FastDateFormat DATE_FORMAT =
		FastDateFormat.getInstance("MM/dd/yyyy HH:mm:ss");

	/** Bot's home directory */
	public static final File BOT_HOME =
		new File(SystemUtils.getUserHome(), ".amnotbot");

	/** Log folder for this server instance */
	private File LOG_HOME;

	static {
		// create folder if it doesn't already exist
		BOT_HOME.mkdirs();
	}

	public BotLogger(String server)
	{
		LOG_HOME = new File(BOT_HOME, "log" + File.separator + server);
		LOG_HOME.mkdirs();

		// create default log
		logs.put(DEFAULT, createLog(LOG_HOME, "_AMNOTBOT"));
	}
	
	protected PrintWriter getDefaultLogger()
	{
		return logs.get(DEFAULT);
	}
	
	public String getLoggingPath()
	{
		return this.LOG_HOME.getAbsolutePath();
	}

	public void log(String msg, boolean timestamp)
	{
		log(getDefaultLogger(), msg, timestamp);
	}

	public void log(String target, String msg, boolean timestamp)
	{
		PrintWriter out = logs.get(target);

		if (out == null) {
			out = createLog(LOG_HOME, target);
			logs.put(target, out);
		}

		log(out, msg, timestamp);
	}

	private void log(PrintWriter out, String msg, boolean stamp)
	{
		String timestamp = DATE_FORMAT.format(new Date());

		BufferedReader reader = new BufferedReader(new StringReader(msg));
		String buf;

		try {
			while ((buf = reader.readLine()) != null) {
				if (stamp)
					out.println("[" + timestamp + "] " + buf);
				else
					out.println(buf);
			}
		} catch (IOException e) {}
	}

	private static PrintWriter createLog(File parent, String target)
	{
		try {
			File f = new File(parent, target);
			f.createNewFile();

			// append and autoflush
			return new PrintWriter(new FileWriter(f, true), true);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create log file", e);
		}
	}
}
