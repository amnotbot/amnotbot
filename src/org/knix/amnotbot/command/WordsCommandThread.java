package org.knix.amnotbot.command;

import org.knix.amnotbot.command.utils.CmdCommaSeparatedOption;
import org.knix.amnotbot.command.utils.CommandOptions;
import org.knix.amnotbot.command.utils.CmdStringOption;
import org.knix.amnotbot.*;
import java.io.File;
import java.util.LinkedList;

import org.knix.amnotbot.config.BotConfiguration;
import org.schwering.irc.lib.IRCUser;

public class WordsCommandThread extends Thread {
	
	private BotConnection con;
	String chan;
	String msg;
	IRCUser user;
	WordCounter wordCounter;
	CommandOptions opts;
	boolean lines;
	String target;

	public WordsCommandThread(BotConnection con
			, String chan
			, IRCUser user
			, String msg
			, boolean lines) 
	{
		this.con = con;
		this.chan = chan;
		this.user = user;
		this.msg = msg;
		this.lines = lines;
		
		opts = new CommandOptions(msg);
		
		opts.addOption( new CmdCommaSeparatedOption("nick") );
		opts.addOption( new CmdCommaSeparatedOption("word") );
		opts.addOption( new CmdStringOption("number") );
		opts.addOption( new CmdStringOption("date") );
		opts.addOption( new CmdStringOption("op") );
		opts.addOption( new CmdStringOption("channel") );
		
		start();
	}

	private boolean init()
	{
		this.opts.buildArgs();

		this.target = this.chan;
		if (this.opts.getOption("channel").hasValue()) {
			this.target = this.opts.getOption("channel").stringValue(); 
		}

		BotLogger.getDebugLogger().debug("Channel = " + this.target);

		if (this.target.charAt(0) != '#') {
			this.con.doPrivmsg(this.chan, "Not a valid channel: (" + this.target + "). Use the 'channel:' option.");
			return false;
		}
		
		String db_file = this.con.getBotLogger().getLoggingPath() + "/" + this.target;		
		if (!this.dbExists(db_file)) {
		    this.con.doPrivmsg(this.chan, "Statistics not available for: (" + this.target + ").");
		    return false;
		}
				
		BotLogger.getDebugLogger().debug( BotConfiguration.getConfig().getString("ignored_words_file") );
		String wCounterImp = BotConfiguration.getConfig().getString("word_counter_imp");
		BotLogger.getDebugLogger().debug(wCounterImp);

		if (wCounterImp.compareTo("textfile") == 0) {
			this.wordCounter = new WordCounterTextFile( 
					BotConfiguration.getConfig().getString("ignored_words_file"),
					db_file);
		} else if (wCounterImp.compareTo("sqlite") == 0) {			
			BotLogger.getDebugLogger().debug("SQLITE");
			this.wordCounter = new WordCounterSqlite(db_file);
		}
		
		return true;
	}
	
	boolean dbExists(String path) 
	{
	    File db_file = new File(path);
	    
	    if (!db_file.exists())
		return false;	    
	    
	    return true;
	}
	
	public void run()
	{
		String words;

		if (!this.init())
		    return;
		
		String nWords = this.opts.getOption("number").stringValue();
		
		int n = nWords == null ? 5 : Integer.parseInt( nWords );
		
		String outMsg;

		String nickList = null;
		if (opts.getOption("nick").hasValue()) {
			nickList = this.opts.getOption("nick").stringValue(" ").toLowerCase();
		}

		if (!this.lines) {		
			if (this.opts.getOption("word").hasValue()) {
				words = this.wordCounter.mostUsedWordsBy(n, 
							this.opts.getOption("word").stringValue(" ").trim(), 
							this.opts.getOption("date").stringValue()
				);
			} else {
				words = this.wordCounter.mostUsedWords(n, nickList,
							this.opts.getOption("date").stringValue()
				);
			}
			outMsg = "Most used words for ";
		} else {
			String op = "";
			if (this.opts.getOption("op").hasValue()) {
				op = this.opts.getOption("op").stringValue();
			}
			if (op.compareTo("avg") == 0) {
				words = this.wordCounter.avgWordsLine(n, nickList,
						this.opts.getOption("date").stringValue()
				);
				outMsg = "Avg. words per line per user for ";
			} else {
				words = this.wordCounter.topLines(n, this.opts.getOption("date").stringValue());
				outMsg = "Lines per user for ";
			}
		}
		
		if (words.length() == 0) {
			this.con.doPrivmsg(this.chan, "Could not find any match!");
		} else {
			LinkedList<String> w = new LinkedList<String>();
			int trunc = 0, truncationConstant = 430;	// irc client truncates everything over 440 chars
			while ((words.length() + outMsg.length()) - trunc > truncationConstant) {
				int truncPos = words.indexOf(' ', (truncationConstant / 2) + trunc);
				w.add( words.substring(trunc, truncPos) );
				trunc = truncPos;
			}
			w.add( words.substring(trunc, words.length()) );

			if (opts.getOption("nick").hasValue()) {
				this.con.doPrivmsg(this.chan, outMsg 
					+ "'" 
					+ opts.getOption("nick").stringValue().trim() 
					+ "': " + w.getFirst());
			} else {
				this.con.doPrivmsg(this.chan, outMsg + "'" + this.target + "': " + w.getFirst());
			}

			for (int j = 1; j < w.size(); ++j) {
				this.con.doPrivmsg(this.chan, w.get(j));

				try {
					Thread.sleep(300 * j);	// avoid being disconnected by flooding
				} catch (InterruptedException e) {
					BotLogger.getDebugLogger().debug(e.getMessage());
					break;
				}
			}
			BotLogger.getDebugLogger().debug(words);
		}

	}	
}
