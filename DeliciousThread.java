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
	
	public DeliciousThread(DeliciousBookmarks delicious, BotConnection con,
			String chan,
			String nick,
			String url,
			String msg,
			boolean showTitle)
	{
		this.con = con;
		this.chan = chan;
		this.nick = nick;
		this.url = url;
		this.delicious = delicious;
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
			for (int i = 0; i < str.length; ++i) {
				tags += " " + str[i].trim().replace(" ", ".");
			}
		}
		
		return tags;
	}
		
	private String getTags()
	{		
		String tags = "";
		CmdCommaSeparatedOption tagOption;		
		
		tagOption = (CmdCommaSeparatedOption)this.opts.getOption("tags");
		if (tagOption.hasValue()) 
			tags += tagOption.stringValue(" ", ".");

		tags += this.getPageTags();
				
		if (tags.trim().length() > 0)
			tags += " " + this.nick;
		else
			tags = this.nick;
		
		return tags;
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
							
		System.out.println("DeliciousThread: tags:" + tags + ":title:" + title + ":length(" + title.length() + ")" + ":comment:" + comment + ":");
		success = false;
		if (this.showTitle && this.isPageTitle())
			this.con.doPrivmsg(this.chan, title);

		success = this.delicious.addPost(this.url, title, comment, tags.trim(), new Date());
						
		if (!success)
			System.out.println("Post failed! :-(");
		else
			System.out.println("Posted!");
	}
}
