package org.knix.amnotbot;

import java.util.Date;

public class DeliciousThread extends Thread 
{	
	private BotConnection con;
	private String chan;
	private String url;	
	private String nick;
	private DeliciousBookmarks delicious;
	private AmnotbotHTMLParser parser;
	private boolean showTitle;
	private CommandOptions opts;
	private int maxTagLength;
	
	public DeliciousThread(DeliciousBookmarks delicious, BotConnection con,
			String chan,
			String nick,
			String url,
			String msg,
			int maxTagLength,
			boolean showTitle)
	{
		this.con = con;
		this.chan = chan;
		this.nick = nick;
		this.url = url;
		this.delicious = delicious;
		this.maxTagLength = maxTagLength;
		this.showTitle = showTitle;
		
		opts = new CommandOptions(msg);
		
		opts.addOption( new CmdCommaSeparatedOption("tags") );
		opts.addOption( new CmdStringOption("title", '"') );
		opts.addOption( new CmdStringOption("comment", '"') );
		
		this.parser = new AmnotbotHTMLParser(this.url);	
		
		start();
	}

	private String getPageTags()
	{
		String tags = "";
		String keywords;

		keywords = this.parser.getKeywords();
		if (keywords != null) {
			String [] str = keywords.split(",");
			if (str.length == 1)
				str = keywords.split(" ");
			for (int i = 0; i < str.length; ++i) {
				tags += " " + str[i].trim().replace(" ", ".");
			}
		}
		
		return tags;
	}
		
	private String getTags()
	{		
		String tmpTags = "";
		String finalTags = "";
		CmdCommaSeparatedOption tagOption;		
		
		tagOption = (CmdCommaSeparatedOption)this.opts.getOption("tags");
		if (tagOption.hasValue()) 
			tmpTags += tagOption.stringValue(" ", ".");

		tmpTags += this.getPageTags();
				
		if (tmpTags.trim().length() > 0)
			tmpTags += " " + this.nick;
		else
			tmpTags = this.nick;

		String [] str = tmpTags.split(" "); 
		for (int i = 0; i < str.length; ++i) {
			if (str[i].length() > this.maxTagLength)
				continue;
			finalTags += " " + str[i].trim();
		}
		
		return finalTags;
	}
		
	private String getTitle()
	{				
		CmdStringOption titleOption;
		
		titleOption = (CmdStringOption)this.opts.getOption("title");
		
		if (titleOption.hasValue())
			return titleOption.stringValue();
		
		String title = this.parser.getTitle();
		if (title != null)			
			return title;
		
		return this.url;		
	}
		
	private boolean isPageTitle()
	{
		if (!this.opts.getOption("title").hasValue() && this.parser.getTitle() != null)
			return true;
		
		return false;
	}
	
	public void run()
	{
		Boolean success;
		String tags;
		String title;
		String comment;
		
		this.opts.buildArgs();
		
		tags = this.getTags();
		title = this.getTitle();
		comment = this.opts.getOption("comment").stringValue();
							
		BotLogger.getDebugLogger().debug("DeliciousThread: tags:" + tags + ":title:" + title + ":length(" + title.length() + ")" + ":comment:" + comment + ":");
		success = false;
		if (this.showTitle && this.isPageTitle())
			this.con.doPrivmsg(this.chan, title);

		success = this.delicious.addPost(this.url, title, comment, tags.trim(), new Date());
						
		if (!success)
			BotLogger.getDebugLogger().debug("Post failed! :-(");
		else
			BotLogger.getDebugLogger().debug("Posted!");
	}
}
