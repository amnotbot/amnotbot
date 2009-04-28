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
package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
import java.net.ConnectException;
import java.util.Hashtable;
import java.util.Vector;

public class DictThread extends Thread
{

    private BotConnection con;
    private String query;
    private String chan;
    private String word;
    private String defaultDict;
    private String defaultStrategy;
    private boolean onlySpelling;
    private DictClient dictClient;
    private Hashtable<String, Class> dictParsers;

    public DictThread(BotConnection con)
    {
        this.con = con;

        this.dictParsers = new Hashtable<String, Class>();

//		this.dictParsers.put("wn", WordNetDictParser.class);
        this.dictParsers.put("vera", DictVeraParser.class);
    }

    public void performQuery(DictClient dictClient,
            String chan,
            String nick,
            String query,
            String defaultDict,
            String defaultStrategy,
            boolean onlySpelling)
    {
        this.chan = chan;
        this.query = query;
        this.defaultDict = defaultDict;
        this.defaultStrategy = defaultStrategy;
        this.onlySpelling = onlySpelling;
        this.dictClient = dictClient;

        this.word = this.getWord(this.query);

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
        String strategy;
        String[] databases;
        String[][] matches;

        databases = new String[]{"all"};
        strategy = this.getStrategy(this.query);
        try {
            matches = this.dictClient.getMatches(databases,
                    strategy, this.word);
        } catch (ConnectException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            return;
        }
        
        switch (matches.length) {
            case 0:
                this.con.doPrivmsg(this.chan,
                        "Could not find a match for word '" + this.word + "'!");
                break;
            case 1:
                if (matches[0][1].equals(this.word) ) {
                    this.con.doPrivmsg(this.chan, "The word '" +
                            this.word + "' is spelled correctly!");
                } else {
                    this.con.doPrivmsg(this.chan, "The word '" +
                            this.word + "' is misspelled or does not exist!");
                    this.con.doPrivmsg(this.chan, "May be you meant: " +
                            matches[0][1]);
                }
                break;
            default:
                String words = new String();
                Vector definitions = this.getDefinitions(true);
                if (!definitions.isEmpty()) {
                    this.con.doPrivmsg(this.chan, "The word '" +
                            this.word + "' is spelled correctly!");
                    words = words.concat("Other similar words are: ");
                } else {
                    this.con.doPrivmsg(this.chan, "The word '" +
                            this.word + "' is misspelled or does not exist!");
                    words = words.concat("May be you meant: ");
                }

                int lines = 0;
                for (int i = 0; i < matches.length; ++i) {
                    words = words.concat(matches[i][1] + " ");
                    if (words.length() >= 80) {
                        this.con.doPrivmsg(this.chan, words);
                        try {
                            // Horrible hack! Throttle in the connection instead!
                            this.sleep(1000);	
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                        words = "";
                        lines++;
                    }
                    if (lines > 10) { // TODO: remove hardcoded value!
                        this.con.doPrivmsg(this.chan,
                                "NOTICE: Too many words! Skipping ...");
                        break;
                    }
                }
                if (words.length() >= 1) {
                    this.con.doPrivmsg(this.chan, words);
                }
        }
    }

    private Vector<DictDefinition> getDefinitions(boolean spell)
    {
        Vector definitions = new Vector();
        String[] databases = {this.getDatabase(this.query, spell)};

        try {
            definitions = this.dictClient.getDefinitions(databases, this.word);
        } catch (ConnectException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        } catch (IllegalArgumentException e) {
            BotLogger.getDebugLogger().debug(e);
        }
        return definitions;
    }

    private void doDefine()
    {
        Vector definitions = this.getDefinitions(false);
        if (definitions.isEmpty()) {
            this.con.doPrivmsg(this.chan, "Could not find word the '" +
                    this.word + "'!");
            return;
        }

        DictDefinition def;
        def = (DictDefinition) definitions.firstElement();

        Class dictClass = this.dictParsers.get(def.getDatabaseShort());
        if (dictClass == null) {
            dictClass = DictDefaultParser.class;
        }

        DictParser parser;
        try {
            parser = (DictParser) dictClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            return;
        }

        Vector parsedDefinitions = parser.firstDefinition(def);
        this.con.doPrivmsg(this.chan, def.getDatabaseLong());
        for (int i = 0; i < parsedDefinitions.size(); ++i) {
            String mString = (String) parsedDefinitions.elementAt(i);
            String[] lines = mString.split("\n");
            
            for (int j = 0; j < lines.length; ++j) {
                this.con.doPrivmsg(this.chan, lines[j]);
                try {
                    this.sleep(1000);
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
        String[] data;

        data = query.split(" ");
        if (spell && data.length == 3) return data[0];
        if (data.length > 1) return data[0];
        return this.getDefaultDatabase();
    }

    private String getDefaultDatabase()
    {
        return this.defaultDict;
    }

    private String getDefaultStrategy()
    {
        String[][] strategies;

        strategies = this.dictClient.getStrategies();
        for (int i = 0; i < strategies.length; ++i) {
            if (this.defaultStrategy.equals(strategies[i][0])) {
                return this.defaultStrategy;
            }
        }
        return strategies[0][0];
    }

    private String getWord(String query)
    {
        String[] data;

        data = query.split(" ");
        if (data.length == 1) return data[0];
        if (data.length > 1) return data[data.length - 1];
        return null;
    }

    /** !spell [db] strategy word **/
    private String getStrategy(String query)
    {
        String[] data;

        data = query.split(" ");
        if (data.length < 2) return this.getDefaultStrategy();
        if (data.length == 2) return data[0];
        if (data.length == 3) return data[1];
        return null;
    }
}
