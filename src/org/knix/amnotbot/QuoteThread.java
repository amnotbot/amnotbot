/*
 * Author: Geronimo Poppino.
 */

package org.knix.amnotbot;

import java.util.Random;

import org.apache.commons.lang.SystemUtils;
import org.schwering.irc.lib.IRCUser;

import SQLite.Database;
import SQLite.Exception;
import SQLite.TableResult;

public class QuoteThread extends Thread {
	
	private BotConnection con;
	private String chan;
	private String db_filename;
	private IRCUser user;
	private Database db;
	private CommandOptions opts;

	
	public QuoteThread(BotConnection con
			, String chan
			, IRCUser user
			, String msg) 
	{
		this.con = con;
		this.chan = chan;
		this.user = user;
		this.db_filename = SystemUtils.getUserHome() + "/" + ".amnotbot" + "/" + "quotes.db";
		this.db = new Database();
		
		opts = new CommandOptions(msg);
		
		opts.addOption( new CmdStringOption("text", '"') );
		opts.addOption( new CmdStringOption("id") );
		opts.addOption( new CmdStringOption("op") );
		
		start();
	}
	
	public void run()
	{	
		try {
			this.db.open(this.db_filename, 0);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			return;
		}
		
		this.opts.buildArgs();
				
		if (this.opts.getOption("op").hasValue()) {
			String op = this.opts.getOption("op").stringValue();
			
			if (op.compareTo("set") == 0) {
				this.createNewQuote(this.opts.getOption("text").stringValue());
			} else if (op.compareTo("del") == 0) {
				this.deleteQuote(this.opts.getOption("id").stringValue());
			} else if (op.compareTo("get") == 0) { 
				this.getRandomQuote();
			} else if (op.compareTo("info") == 0) { 
				this.getInfoAboutQuote(this.opts.getOption("id").stringValue());
			}
		} else {
			this.getRandomQuote();
		}
		
		try {
			this.db.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
	
	private void createNewQuote(String text)
	{
		String query;
		
		if (text == null)
			return;
		
		query = "INSERT INTO quotes (nick, desc) VALUES (" 
			+ "'" + this.user.getNick() + "'" 
			+ ", " + "\"" + text + "\"" + ");";
		
		this.execQuery(query);
		
		String msg;
		if (this.db.changes() > 0) {
		    msg = "Quote (" + this.db.last_insert_rowid() 
				+ ") successfully created!";
		    this.con.doPrivmsg(this.chan, msg);
		} else {
		    msg = "Quote creation failed!";
		    this.con.doPrivmsg(this.chan, msg);			    
		}
	}
	
	private void deleteQuote(String id)
	{
		String query;
		
		System.out.println("id = " + id);
		
		if (id == null)
			return;
		
		query = "DELETE FROM quotes WHERE id=" + Integer.valueOf(id);
		
		this.execQuery(query);	
	}
	
	private void execQuery(String query)
	{
		System.out.println(query);
		
		try {
			this.db.exec(query, new QuoteTableFmt());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}				
	}
	
	private TableResult runQuery(String query)
	{
		TableResult results;
			
		try {
			results = this.db.get_table(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		if (results.nrows <= 0)
			return null;

		return results;
	}

	private void getRandomQuote()
	{
		Random rand;
		String query;
		TableResult results;

		query = "SELECT * FROM quotes";

		results = this.runQuery(query);

		if (results != null) {
			rand = new Random();
			System.out.println(results.nrows);
			String [] r = (String[]) results.rows.get( rand.nextInt(results.nrows) );
			String msg = "(" + r[0] + ")" + ": " + "\"" + r[2] + "\"";
			this.con.doPrivmsg(this.chan, "Quote " + msg);
		}
	}

	private void getInfoAboutQuote(String id)
	{
		String query;
		TableResult results;
		
		System.out.println("id = " + id);
		
		if (id == null)
			return;
		
		query = "SELECT * FROM quotes WHERE id=" + Integer.valueOf(id);
		
		results = this.runQuery(query);
		
		if (results != null) {
			String [] r = (String[]) results.rows.get(0);
			this.con.doPrivmsg(this.chan, 
				"Quote (" + id + ") submitted by " 
				+ r[1]
			);
		}
	}
}

class QuoteTableFmt implements SQLite.Callback
{	
	public QuoteTableFmt()
	{
	}

	public void columns(String[] arg0) 
	{		
	}

	public boolean newrow(String[] arg0) 
	{		
		return false;
	}

	public void types(String[] arg0) 
	{
		// TODO Auto-generated method stub		
	}
}
