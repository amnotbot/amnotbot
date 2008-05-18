/*
 * Copyright (c) 2007 gresco 
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

package org.knix.amnotbot;

import java.net.ConnectException;
import java.util.Hashtable;
import java.util.Vector;


public class DictThread extends Thread
{    
	private static final String DEFAULT_DICTIONARY = "!"; 
	private static final String DEFAULT_STRATEGY = "lev";

	private BotConnection con;
	private String query;
	private String chan;
	private String word;
	private boolean onlySpelling;

	private DICTClient dictClient;

	private Hashtable<String, Class> dictParsers;


	public DictThread(BotConnection con) {
		this.con = con;

		this.dictParsers = new Hashtable<String, Class>();

		this.dictParsers.put("wn", WordNetDictParser.class);
		this.dictParsers.put("vera", VeraDictParser.class);
	}

	public void performQuery(DICTClient dictClient,
			String chan,
			String nick,
			String query,
			boolean onlySpelling)
	{
		this.chan = chan;
		this.query = query;
		this.onlySpelling = onlySpelling;
		this.dictClient = dictClient;

		this.word = this.getWord(this.query);
		System.out.println("query :->" + this.query);
		System.out.println("word :->" + this.word);
		System.out.println("database :->" + this.getDatabase(this.query, onlySpelling));

		start();
	}

	public void run()
	{
		if (this.onlySpelling) {
			this.doSpell();
		} else {
			this.doDefine();
		}
	}

	private void doSpell()
	{
		String [] databases;
		String strategy;
		String database;
		String [][] matches;

		database = this.getDatabase(this.query, true);

		databases = new String[]{database};
		strategy = this.getStrategy(this.query);

		try {
			matches = this.dictClient.getMatches(databases, strategy, this.word);
		} catch (ConnectException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			this.con.doPrivmsg(this.chan, e.getMessage());
			return;
		}

		System.out.println("databases = " + databases[0] + " strategy = " + strategy + " word = " + this.word);
		System.out.println("matches #" + matches.length);

		switch (matches.length) {
		case 0:
			this.con.doPrivmsg(this.chan, "Could not find a match for word '" + this.word + "'!");
			break;

		case 1:
			if (matches[0][1].compareTo(this.word) == 0) {
                		this.con.doPrivmsg(this.chan, "The word '" + this.word + "' is spelled correctly!");
                		this.con.doPrivmsg(this.chan, "Could not find more matches. You can always try with another dictionary or strategy.");
            		} else {
                		this.con.doPrivmsg(this.chan, "The word '" + this.word + "' is misspelled or does not exist!");
                		this.con.doPrivmsg(this.chan, "Might be you meant: " + matches[0][1]);
            		}
			break;

		default:
			String words = new String();
			Vector definitions = this.getDefinitions(true);

			if (!definitions.isEmpty()) {
				this.con.doPrivmsg(this.chan, "The word '" + this.word + "' is spelled correctly!");
				words = words.concat("Other similar words are: ");
			} else {
				this.con.doPrivmsg(this.chan, "The word '" + this.word + "' is misspelled or does not exist!");
				words = words.concat("Might be you meant: ");
			}

			int lines = 0;
			for (int i = 0; i < matches.length; ++i) {
				words = words.concat(matches[i][1] + " ");
				if (words.length() >= 80) {
					this.con.doPrivmsg(this.chan, words);
					try {
						this.sleep(1000);	// Horrible hack! Throttle in the connection instead!
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					words = "";
					lines++;
				}

				if (lines > 10) { // TODO: remove hardcoded value!
					this.con.doPrivmsg(this.chan, "NOTICE: Too many words! Skipping ...");
					break;
				}
			}

			if (words.length() >= 1) {
				this.con.doPrivmsg(this.chan, words);
			}
		}

	}

	private Vector<Definition> getDefinitions(boolean spell)
	{
		Vector definitions = new Vector();
		String [] databases = { this.getDatabase(this.query, spell) };

		try {
			definitions = this.dictClient.getDefinitions(databases, this.word);
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			this.con.doPrivmsg(this.chan, e.getMessage());
		}

		return definitions;
	}


	private void doDefine()
	{
		Vector definitions = this.getDefinitions(false);        

		if (definitions.isEmpty()) {
			this.con.doPrivmsg(this.chan, "Could not find word the '" + this.word + "'!");
			return;
		}

		Definition myDefinition = (Definition) definitions.firstElement();	// Just get the first definition for now.       

		Class dictClass = this.dictParsers.get(myDefinition.getDatabaseShort());

		if (dictClass == null)
			dictClass = DefaultDictParser.class;

		DictParser parser;
		try {
			parser = (DictParser) dictClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}

		Vector parsedDefinitions = parser.firstDefinition(myDefinition);

		this.con.doPrivmsg(this.chan, myDefinition.getDatabaseLong());
		for(int i = 0; i < parsedDefinitions.size(); ++i) {
			String mString = (String) parsedDefinitions.elementAt(i);
			String [] lines = mString.split("\n");   

			for (int j = 0; j < lines.length; ++j) {
				this.con.doPrivmsg(this.chan, lines[j]);
				try {
					this.sleep(1000);	// Horrible hack! Throttle in the connection instead!
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/** !spell [db] strategy word **/
	/** !dict [db] word **/
	private String getDatabase(String query, boolean spell)
	{
		String [] data;

		data  = query.split(" ");

		if (spell) {
			if (data.length == 3) {
				return data[0];
			} else {
				return this.getDefaultDatabase();
			}
		} else {
			if (data.length > 1)
				return data[0];
			else
				return this.getDefaultDatabase();
		}
	}

	private String getDefaultDatabase()
	{
		return this.DEFAULT_DICTIONARY;
	}

	/* Slow */
	private String getDefaultStrategy()
	{
		String [][] strategies;
		String strategy;

		strategies = this.dictClient.getStrategies();

		for (int i = 0; i < strategies.length; ++i) {
			System.out.println("Strategies " + strategies[i][0]);

			if (this.DEFAULT_STRATEGY.compareTo(strategies[i][0]) == 0) {
				return this.DEFAULT_STRATEGY;
			}
		}

		return strategies[0][0];
	}

	private String getWord(String query)
	{
		String [] data;

		data = query.split(" ");
		if (data.length == 1)
			return data[0];
		else if (data.length > 1)
			return data[data.length - 1];

		return null;
	}

	/** !spell [db] strategy word **/
	private String getStrategy(String query)
	{
		String [] data;

		data = query.split(" ");
		if (data.length < 2)
			return this.getDefaultStrategy();
		else if (data.length == 2)
			return data[0];
		else if (data.length == 3)
			return data[1];

		return null;
	}
}
