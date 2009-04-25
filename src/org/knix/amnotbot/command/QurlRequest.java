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

package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 * TODO - Use Jakarta's HttpClient (maybe)
 */
import org.apache.log4j.Logger;

/**
 * Request a shortened URL from Qurl.org
 * 
 * @author Jimmy Mitchener
 * @version 0.01
 */
public class QurlRequest extends Thread
{
	/** Connection to print messages to. */
	private BotConnection con;

	/** Channel to print to. */
	private String chan;

	/** User that requested URL. */
	private String nick;

	/** URL to be shortened. */
	private String query;

	/** Regex to grab URLs from qurl.org responses */
	public static final String REGEX = ".*a href=\"(http://qurl.org.*)\".*";
    
    private Logger logger;

	/**
	 * Create a new QurlRequest thread.
	 * 
	 * @param con Connection to print the URL to.
	 * @param chan Channel to print the URL.
	 * @param nick User that requested the shortened URL.
	 * @param query URL to be shortened.
	 */
	public QurlRequest(BotConnection con,
			String chan,
			String nick,
			String query)
	{
		this.con = con;
		this.chan = chan;
		this.nick = nick;
		this.query = query;
        
        this.logger = BotLogger.getDebugLogger();

		start();
	}

	public void run()
	{
		try {
			String encoded = URLEncoder.encode(query, "UTF-8");

			URL url = new URL("http://qurl.org/submit.jsp?url=" + encoded);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					http.getInputStream()));

			String buf = reader.readLine();

			if (buf.matches(REGEX)) {
				String qurl = buf.replaceFirst(REGEX, "$1");
				String domain = query.replaceFirst(
						".*https?://([\\w.-]+)/.*", "<$1>");

				// TODO - probably a better way of going about this
				if (domain.equals(query))
					domain = "";

				con.doPrivmsg(chan, nick + ": " + qurl + " " + domain);
			}
		} catch (Exception e) {
            this.logger.debug(e.getMessage());
		}
	}
}
