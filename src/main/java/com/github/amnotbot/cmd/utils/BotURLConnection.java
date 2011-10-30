package com.github.amnotbot.cmd.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author gpoppino
 */
public class BotURLConnection 
{
    
    private URL url;
    
    public BotURLConnection(URL url)
    {
        this.url = url;
    }
    
    private URLConnection startConnection()
            throws IOException {
        URLConnection conn;

        conn = this.url.openConnection();
        conn.addRequestProperty("Referer", "http://packetpan.org");

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

}
