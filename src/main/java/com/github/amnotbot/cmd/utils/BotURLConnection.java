/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
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
package com.github.amnotbot.cmd.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.io.OutputStreamWriter;

/**
 *
 * @author gpoppino
 */
public class BotURLConnection 
{
    
    private final URL url;
    private final HashMap<String, String> headers;
    
    public BotURLConnection(URL url)
    {
        this.url = url;
        this.headers = new HashMap<>();
    }
    
    private URLConnection startConnection()
            throws IOException {
        URLConnection conn;

        conn = this.url.openConnection();
        conn.setRequestProperty("User-Agent", "amnotbot/1.0");

        return conn;
    }

    private String makeQuery(URLConnection conn)
            throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }
    
    public String fetchURL() 
            throws IOException
    {
        return this.makeQuery( this.startConnection() );
    }

    private URLConnection doPost(HttpURLConnection conn, String data)
        throws IOException
    {
        try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream())) {
            out.write(data);
            out.flush();
        }
        return conn;
    }

    public void addHeader(String key, String value)
    {
        this.headers.put(key, value);
    }

    public String postToURL(String data)
            throws IOException
    {
        HttpURLConnection conn;

        conn = (HttpURLConnection) this.startConnection();
        conn.setRequestMethod("POST");
        headers.forEach(conn::setRequestProperty);
        conn.setDoOutput(true);

        return this.makeQuery( this.doPost(conn, data) );
    }

}
