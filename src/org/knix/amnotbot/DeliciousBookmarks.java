package org.knix.amnotbot;

import java.util.Date;

import org.knix.amnotbot.config.BotConfiguration;

import del.icio.us.Delicious;

public class DeliciousBookmarks {
	
	private Delicious delicious;
	
	public DeliciousBookmarks()
	{
	    String user = BotConfiguration.getConfig().getString("delicious_user");
	    String pass = BotConfiguration.getConfig().getString("delicious_pass");
	    
	    this.delicious = new Delicious(user, pass);
	}
	
	public boolean addPost(String url, String description, String extended, String tags, Date date)
	{
		String d = date.toString();
		String mm = d.substring(4, 7);
		String dd = d.substring(8, 10);
		String yyyy = d.substring(d.length() - 4, d.length());
		
		tags += " " + dd + "-" + mm + "-" + yyyy;
		tags += " " + mm + "-" + yyyy;
		tags += " " + yyyy;
		
		System.out.println("URL: " + url);
		System.out.println("Description: " + description);
		System.out.println("Extended: " + extended);
		System.out.println("Tags: " + tags);
		System.out.println("Date: " + date);
		
		return this.delicious.addPost(url, description, extended, tags, date);
	}		
}
