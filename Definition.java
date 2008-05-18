/*
 *  Defintion.java
 *  
 *  Copyright (c) 2002 Daniel Tams <dantams@myrealbox.com>
 *	All Rights Reserved.
 *
 * 	Redistribution and use in source and binary forms, with or without 
 *	modification, are permitted provided that the following conditions are met:
 *
 *	  * Redistributions of source code must retain the above copyright notice, 
 *		this list of conditions and the following disclaimer.
 *	  * Redistributions in binary form must reproduce the above copyright 
 *		notice, this list of conditions and the following disclaimer in the 
 *		documentation and/or other materials provided with the distribution.
 *	  * The name of the copyright owner may not be used to endorse or promote 
 *		products derived from this software without specific prior written 
 *		permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY EXPRESS OR
 *	IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO 
 *	EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *	INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *	LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *	OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *	LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *	EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.knix.amnotbot;

/**
 *  This class constitutes a full definition for a word as received from the 
 *  {@link org.knix.amnotbot.DICTClient DICTClient} with all the data connected to it. 
 *  This means the short name of the database it was gotten from, its long name,
 *  the word that was being queried and its definition.
 *  @see org.knix.amnotbot.DICTClient
 */
public class Definition {
	private String databaseShort;
	private String databaseLong;
	private String word;
	private String definition;

	/**
	 *  Constructs a new definition object.
	 *  @param databaseShort The short name of the database, for example
	 *  "web1913".
	 *  @param databaseLong The full name of the database which the definition
	 *  comes from, for example "Webster's Revised Unabridged Dictionary
	 *  (1913)".
	 *  @param word The word being defined, for example "sausage".
	 *  @param definition The full definition of the word.
	 */
	public Definition(String databaseShort, String databaseLong, String word, 
			String definition) {
		this.databaseShort = databaseShort;
		this.databaseLong = databaseLong;
		this.word = word;
		this.definition = definition;
	}

	/**
	 *  Returns the short name of the database from which the defintion was
	 *  received.
	 *  @return The short name of the database.
	 */
	public String getDatabaseShort() {
		return databaseShort;
	}

	/**
	 *  Gets the full name of the database from which this definition was
	 *  received.
	 *  @return The full name of the database.
	 */
	public String getDatabaseLong() {
		return databaseLong;
	}

	/**
	 *  Returns the word which is being defined.
	 *  @return The word being defined.
	 */
	public String getWord() {
		return word;
	}

	/**
	 *  Returns the actual definition of the word. This is usually formatted 
	 *  with newlines and indents, etc. Some databases put a '{' and '}' around 
	 *  references to other words.
	 *  @return The defintion of the word as given by that database.
	 */
	public String getDefinition() {
		return definition;
	}
}
