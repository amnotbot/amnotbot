package org.knix.amnotbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleSearchThread extends Thread {
    
	private BotConnection con;
	private String query;
	private String chan;
	private String nick;

    
	public GoogleSearchThread(BotConnection con,
			String chan,
			String nick,
			String query)
	{
		this.con = con;
		this.chan = chan;
		this.nick = nick;
		this.query = query;
	
		start();
	}
    
	public void run()
	{
		this.makeQuery();
	}
    
	public void makeQuery()
	{
		URL url;
		try {
			String urlString = 
				"http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" 
				+ URLEncoder.encode(this.query, "UTF-8");
			url = new URL(urlString);
		} catch (MalformedURLException ex) {
			System.out.println(ex.getMessage());
			return;
		} catch (UnsupportedEncodingException ex) {
			System.out.println(ex.getMessage());
			return;
		}	

		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			return;
		}

		connection.addRequestProperty("Referer",
			"http://knix.mine.nu/index.html");

		String line;
		StringBuilder builder = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			return;
		}

		JSONObject json;
		try {
			json = new JSONObject(builder.toString());
		} catch (JSONException ex) {
			System.out.println(ex.getMessage());
			return;
		}
	
		try {
			this.con.doPrivmsg(this.chan, json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("titleNoFormatting"));
			this.con.doPrivmsg(this.chan, json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("url"));
		} catch (JSONException ex) {
			System.out.println(ex.getMessage());
		}
    }
}
