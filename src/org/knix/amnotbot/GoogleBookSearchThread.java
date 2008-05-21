package org.knix.amnotbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleBookSearchThread extends Thread {
    
	private BotConnection con;
	private String query;
	private String chan;
	private String nick;

    
	public GoogleBookSearchThread(BotConnection con,
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
				"http://ajax.googleapis.com/ajax/services/search/books?v=1.0&q=" 
				+ URLEncoder.encode(this.query, "UTF-8");
			url = new URL(urlString);
		} catch (MalformedURLException ex) {
			System.err.println(ex.getMessage());
			return;
		} catch (UnsupportedEncodingException ex) {
			System.err.println(ex.getMessage());
			return;
		}	

		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
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
			System.err.println(ex.getMessage());
			return;
		}

		JSONObject json;
		try {
			json = new JSONObject(builder.toString());
		} catch (JSONException ex) {
			System.err.println(ex.getMessage());
			return;
		}
	
		try {
			this.con.doPrivmsg(this.chan, json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("titleNoFormatting"));
			this.con.doPrivmsg(this.chan, json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("unescapedUrl"));

			String info;
			info = "by " + json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("authors");
			info += " - " + json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("bookId");
			info += " - " + json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("publishedYear");
			info += " - " + json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).optString("pageCount");
			info += " pages";

			this.con.doPrivmsg(this.chan, info);
		} catch (JSONException ex) {
			System.err.println(ex.getMessage());
		}
    }
}
