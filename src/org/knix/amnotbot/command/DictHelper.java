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
import org.knix.amnotbot.config.BotConfiguration;

public class DictHelper {

	private BotConnection con;
	private DictThread dictThread;
	private DICTClient dictClient;
	
	private String defaultServer;
	private String defaultDict;
	private String defaultStrategy;

	public DictHelper(BotConnection con) {
		this.con = con;      
		this.dictThread = new DictThread(this.con);
		
		this.defaultServer = BotConfiguration.getConfig().getString("dictionary_server");
		this.defaultDict = BotConfiguration.getConfig().getString("default_dictionary");
		this.defaultStrategy = BotConfiguration.getConfig().getString("default_dictionary_strategy");
	}

	public void runQuery(String chan, String nick, String query, boolean isSpelling)
	{
		if(this.dictThread.isAlive()) {
			BotLogger.getDebugLogger().debug("Query still running. Skipping: " + query);
			return; 
		}

		if (this.dictThread.getState() == Thread.State.TERMINATED) {
			this.dictThread = null;
			this.dictThread = new DictThread(this.con);
		}

		if (this.initDictClient()) {
			this.dictThread.performQuery(this.dictClient, 
				chan,
				nick, 
				query, 
				this.defaultDict, 
				this.defaultStrategy, isSpelling);
		}
	}

	private boolean initDictClient()
	{
		if (this.dictClient == null) {
			try {
				this.dictClient  = new DICTClient(this.defaultServer);
			} catch (Exception ex) {
				BotLogger.getDebugLogger().debug(ex.getMessage());
				return false;
			}
		}

		return true;
	}
}
