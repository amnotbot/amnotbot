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


/**
 * Request a shortened URL from Qurl.org
 * 
 * @author Jimmy Mitchener
 */
public class QurlRequest extends Thread
{

    private BotMessage msg;
    public static final String QURL_REGEX = ".*a href=\"(http://qurl.org.*)\".*";
 
    public QurlRequest(BotMessage msg)
    {
        this.msg = msg;

        start();
    }

    public void run()
    {
        try {
            String query = this.msg.getText();
            String encoded = URLEncoder.encode(query, "UTF-8");

            URL url = new URL("http://qurl.org/submit.jsp?url=" + encoded);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    http.getInputStream()));

            String buf = reader.readLine();
            if (buf.matches(QURL_REGEX)) {
                String qurl = buf.replaceFirst(QURL_REGEX, "$1");
                String domain;               
                domain = query.replaceFirst(".*https?://([\\w.-]+)/.*",
                                                                        "<$1>");
                if (domain.equals(query)) {
                    domain = "";
                }

                this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                        this.msg.getUser().getNick() + ": " + qurl +
                        " " + domain);
            }
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e);
        }
    }
}
