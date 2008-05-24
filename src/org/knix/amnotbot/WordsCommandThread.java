package org.knix.amnotbot;

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

	private void init()
	{
		System.out.println( BotConfiguration.getConfig().getString("ignored_words_file") );
		String wCounterImp = BotConfiguration.getConfig().getString("word_counter_imp");
		System.out.println(wCounterImp);

		this.opts.buildArgs();

		this.target = this.chan;
		if (this.opts.getOption("channel").hasValue()) {
			this.target = this.opts.getOption("channel").stringValue(); 
		}

		System.out.println("Channel = " + this.target);
		
		if (wCounterImp.compareTo("textfile") == 0) {
			this.wordCounter = new WordCounterTextFile( 
					BotConfiguration.getConfig().getString("ignored_words_file"),
					this.con.getBotLogger().getLoggingPath() + "/" + this.target);
		} else if (wCounterImp.compareTo("sqlite") == 0) {			
			System.out.println("SQLITE");
			this.wordCounter = new WordCounterSqlite(
					this.con.getBotLogger().getLoggingPath() + "/" + this.target 
			);
		}
	}
	
	public void run()
	{
		String words;

		this.init();
		
		String nWords = this.opts.getOption("number").stringValue();
		
		int n = nWords == null ? 5 : Integer.parseInt( nWords );
		
		String msg;
		if (!this.lines) {		
			if (this.opts.getOption("word").hasValue()) {
				words = this.wordCounter.mostUsedWordsBy(n, 
							this.opts.getOption("word").stringValue(" ").trim(), 
							this.opts.getOption("date").stringValue()
				);
			} else {
				words = this.wordCounter.mostUsedWords(n, 
							this.opts.getOption("nick").stringValue(" "),
							this.opts.getOption("date").stringValue()
				);
			}
			msg = "Most used words for ";
		} else {
			String op = "";
			if (this.opts.getOption("op").hasValue()) {
				op = this.opts.getOption("op").stringValue();
			}
			if (op.compareTo("avg") == 0) {
				words = this.wordCounter.avgWordsLine(n, 
						this.opts.getOption("nick").stringValue(" "),
						this.opts.getOption("date").stringValue()
				);
				msg = "Avg. words per line per user for ";
			} else {
				words = this.wordCounter.topLines(n, this.opts.getOption("date").stringValue());
				msg = "Lines per user for ";
			}
		}
		
		if (words.length() == 0) {
			this.con.doPrivmsg(this.chan, "Could not find any match!");
		} else {
			LinkedList<String> w = new LinkedList<String>();
			int trunc = 0, truncationConstant = 430;	// irc client truncates everything over 440 chars
			while ((words.length() + msg.length()) - trunc > truncationConstant) {
				int truncPos = words.indexOf(' ', (truncationConstant / 2) + trunc);
				w.add( words.substring(trunc, truncPos) );
				trunc = truncPos;
			}
			w.add( words.substring(trunc, words.length()) );

			if (opts.getOption("nick").hasValue()) {
				this.con.doPrivmsg(this.chan, msg 
					+ "'" 
					+ opts.getOption("nick").stringValue().trim() 
					+ "': " + w.getFirst());
			} else {
				this.con.doPrivmsg(this.chan, msg + "'" + this.target + "': " + w.getFirst());
			}

			for (int j = 1; j < w.size(); ++j) {
				this.con.doPrivmsg(this.chan, w.get(j));
			}
			System.out.println(words);
		}

	}	
}
