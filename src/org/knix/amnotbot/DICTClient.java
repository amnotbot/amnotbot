/*
 *	DICTClient.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import org.knix.amnotbot.AboutDict;
import org.knix.amnotbot.Definition;

/**
 *	This class establishes a connection to a DICT server, gets the
 *	defintions for a word, returns them in the form of a
 *	{@link quikdic.Definition Definition} object and throws exceptions when
 *	there are problems with the server or connection.
 */
public class DICTClient {
	//  The statndard port to be used to connect to DICT servers, as deifned in
	//  RFC2229
	private static final int STANDARD_PORT = 2628;

	//	The client identification string sent to the server upon initial 
	//	connection
	private static final String CLIENT_INFO = AboutDict.APP_NAME_STRING + " " + AboutDict.VERSION_STRING;

	//  The standard commands as per RFC2229 that are sent to the DICT server.
	private static final String CLIENT_COMMAND = "CLIENT";
	private static final String DEFINE_COMMAND = "DEFINE";
	private static final String MATCH_COMMAND = "MATCH";
	private static final String SHOW_INFO_COMMAND = "SHOW INFO";
	private static final String SHOW_DATABASES_COMMAND = "SHOW DB";
	private static final String SHOW_STRATEGIES_COMMAND = "SHOW STRAT";
	private static final String SHOW_SERVER_INFO_COMMAND = "SHOW SERVER";
	private static final String QUIT_CONNECTION_COMMAND = "QUIT";

	//  The standard response codes received from the server as per RFC 2229.
	private static final String DATABASES_AVAILABLE_RESPONSE = "110";
	private static final String STRATEGIES_AVAILABLE_RESPONSE = "111";
	private static final String DATABASE_INFORMATION_RESPONSE = "112";
	private static final String SERVER_INFORMATION_RESPONSE = "114";
	private static final String DEFINITIONS_FOUND_RESPONSE = "150";
	private static final String MATCHES_FOUND_RESPONSE = "152";
	private static final String SUCCESSFUL_CONNECTION_RESPONSE = "220";
	private static final String COMMAND_COMPLETED_RESPONSE = "250";
	private static final String INVALID_DATABASE_RESPONSE = "550";
	private static final String INVALID_STRATEGY_REPONSE = "551";
	private static final String NO_MATCH_RESPONSE = "552";
	private static final String NO_DATABASES_AVAILABLE_RESPONSE = "554";
	private static final String NO_STRATEGIES_AVAILABLE_RESPONSE = "555";

	//  the port to be used
	private int port = STANDARD_PORT;

	//  This is a unique message-id similar to the specification in RFC822, 
	//  which is received from the server upon successful connection. For 
	//  example: <912771.9627.1019399833@miranda.org>
	private String messageID;

	//  The capabilities string received from the server upon successful
	//  connection describing its capabilities. See RFC2229 for more info.
	private String capabilities;

	// The string that gives a short info on the server, received upon initial 
	//  conection.
	private String shortServerInfo;

	private BufferedReader in;

	private PrintWriter out;

	//  This is the address of the server this client is to connect to.
	private InetAddress address;

	//  This holds the socket connection to the server.
	private Socket socket;

	//  This holds the list of dictionary databases that the server gives access
	//  to. Its two-dimensional, [x][0] is the short name, [x][1] is the full 
	//  dictionary name.
	private String[][] databases;

	//  This holds the various strategies that the server has to offer for
	//  matching. [x][0]: short name, [x][1]: full name.
	private String[][] strategies;

	/**
	 * 	Attempts to establish a connection to the DICT server given as parameter
	 *	and throws an exception if unsuccessful. With this constructor, the
	 *  client will connect through the standard port for DICT traffic, 2628, as
	 *  specified in RFC 2229.
	 *	@param server This is the host name (for example "dict.org").
	 *	@throws UnknownHostException If the client cannot resolve the host name.
	 *	@throws ConnectException Thrown if a socket could not be established to
	 *		the specified server using the standard port, if no reply is
	 *		received from the server upon connecting or if the server signals
	 *		that it does not accept the connection (either because access is
	 *		denied, server is shutting down or service is temporarily
	 *		unavailable).  Details always follow with the exception.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>
	 */
	public DICTClient(String server) throws UnknownHostException, 
	ConnectException {
		this(server, STANDARD_PORT);
	}

	/**
	 *  Does exactly the same as previous constructor, just that the port
	 *  specified will be used.
	 *  @param server This is the host name of the server to connect to.
	 *  @param port The port number to use.
	 *	@throws UnknownHostException If the client cannot resolve the host name.
	 *	@throws ConnectException Thrown if a socket could not be established to
	 *		the specified server using the specified port, if no reply is
	 *		received from the server upon connecting or if the server signals
	 *		that it does not accept the connection (either because access is
	 *		denied, server is shutting down or service is temporarily
	 *		unavailable).  Details always follow with the exception.
	 */
	public DICTClient(String server, int port) throws UnknownHostException, 
	ConnectException {
		this.port = port;
		try {
			address = InetAddress.getByName(server);
		} catch(UnknownHostException e) {
			throw new UnknownHostException("Could not resolve host " + server);
		}
		connect();
		databases = receiveDatabases();
		strategies = receiveStrategies();
	}

	/**
	 *  This returns the short information on the server, received upon
	 *  connection (for example "miranda.org dictd 1.6.92/rf on Linux 2.2.19").
	 *  @return String containing short info on server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.1
	 */
	public String getShortServerInfo() {
		return shortServerInfo;
	}

	/**
	 *  Returns a string describing the capabilites of the server, for example: 
	 *  "&lt;auth.mime&gt;".
	 *  @return The capabilities string of the server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.1
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 *  Returns the unique message id received from the server upon successful 
	 *  connection (for example "&lt;1146495.5810.1019655771@miranda.org&gt;").
	 *  @return The message id received from the server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.1
	 */
	public String getMessageID() {
		return messageID;
	}

	/**
	 *  This returns the long info on the server.
	 *  @return String containing full info on the server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.5.4
	 */
	public synchronized String getFullServerInfo() {
		try {
			return receiveServerInfo();
		} catch (ConnectException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not receive server info.");
		}
	}

	/**
	 *  Returns information received from the server about the specified
	 *  database. 
	 *  @param database The short name of the database you want the info on (for
	 *  example "web1913").
	 *  @return The string containing the full info on the database. It is
	 *  usually several lines of text and is usually formatted with newlines and
	 *  spaces. Sometimes it has '{' and '}' characters surrounding references
	 *  to words defined in that dictionary.
	 *  @throws ConnectException if there was an error in connecting to the
	 *  server.
	 *  @throws IllegalArgumentException if the database given as a parameter is
	 *  not recognized by the server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.5.3
	 */
	public synchronized String getDBInfo(String database) throws 
	ConnectException, IllegalArgumentException {
		return receiveDBInfo(database);
	}

	/**
	 *  Returns a two-dimensional array of <code>String</code>s, containing the 
	 *  names of all the databases the server has to offer. <code>[i][0]</code> 
	 *  contains the short name, <code>[i][1]</code> has the full name.
	 *  @return A two-dimensional array of <code>String</code>s containing the 
	 *  short and long names of all databases offered by the server. 
	 */
	public String[][] getDatabases() {
		return databases;
	}

	/**
	 *  Returns a two-dimensional array of <code>String</code> objects, 
	 *  describing all the match strategies the server has to offer. 
	 *  <code>[i][0]</code> contains the name of the strategy, 
	 *  <code>[i][1]</code> contains a short description of that strategy.
	 *  @return A two-dimensional containing the data on all the strategies the
	 *  server has to offer.
	 */
	public String[][] getStrategies() {
		return strategies;
	}

	/**
	 *  Returns all the defintions gotten from the server for a specific word.
	 *  @param databases An array of all the databases to use in looking
	 *  up the word. The short name of the database should be used. If you
	 *  want use all available databases, the string "*" will be enough.
	 *  @param word The word or phrase to look up.
	 *  @return A <code>Vector</code> with <code>Definition</code> objects.
	 *  There is one <code>Defintion</code> object for each defintion received.
	 *  @throws IllegalArgumentException if any of the databases given as an
	 *  argument is not recognized by the server.
	 *  @throws ConnectException if there occurs any error in connecting to the
	 *  server.
	 *  @see quikdic.Definition
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.2
	 */
	public synchronized Vector getDefinitions(String[] databases, String word) 
	throws IllegalArgumentException, ConnectException {
		return receiveDefinitions(databases, word);
	}

	/**
	 *  Returns all the matches gotten from the server for a specific word.
	 *  @param databases An array of all the databases to use in matching
	 *  up the word. The short name of the database should be used. If you
	 *  want use all available database, the string "*" will be enough.
	 *  @param strategy The name of the strategy to be used. If the default
	 *  strategy is to be used (usually best), this should be ".".
	 *  @param word The word or phrase to match up.
	 *  @return A two-dimensional array of <code>String</code> objects
	 *  containing the matches found. [i][0] contains the short name of the
	 *  database in which the match was found and [i][1] contains the match.
	 *  @throws IllegalArgumentException if any of the databases or the strategy
	 *  given as an argument is not recognized by the server.
	 *  @throws ConnectException if there occurs any error in connecting to the
	 *  server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.3
	 */
	public synchronized String[][] getMatches(String[] databases, 
			String strategy, String word) throws IllegalArgumentException, 
			ConnectException {
		return receiveMatches(databases, strategy, word);
	}

	/**
	 *  Closes the connection to the server, should always be used when 
	 *  finishing a session with a server.
	 *  @see <a href="rfc2229.txt">RFC 2229</a>, section 3.9
	 */
	public synchronized void close() {
		send(QUIT_CONNECTION_COMMAND);
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Socket closing was unsuccesful");
		}
	}

	private void send(String command) {
//		System.out.println(command);    // XXX
		out.println(command);
	}

	private String receive() {
		try {
			String text = in.readLine();
//			System.out.println(text);   // XXX
			return text;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Unexpected exception: IOException in DICTClient.receive()."
			);
		}
	}

	private void connect() throws ConnectException {
		String banner;
		String responseCode;

		try {
			socket = new Socket(address, port);
		} catch(IOException e) {
			throw new ConnectException("Could not connect to " + address + ":" +
					port);
		}
		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8")), true);
		} catch(IOException e) {
			close();
			e.printStackTrace();
			System.exit(1);
		}

		//  This is the initial connection banner described in RFC 2229 section
		//  3.1.
		banner = receive();
		responseCode = banner.substring(0, 3);
		if (!responseCode.equals(SUCCESSFUL_CONNECTION_RESPONSE)) {
			close();
			throw new ConnectException(
					"Error connecting to server, following response received: " 
					+ banner);
		} else {
			int index = banner.lastIndexOf("<");
			messageID = banner.substring(index);
			capabilities = banner.substring(banner.indexOf("<"), index).trim();
			shortServerInfo = banner.substring(4, banner.indexOf("<")).trim();
		}

		//  Send client information
		send(CLIENT_COMMAND + " " + CLIENT_INFO);
		receive();
	}

	private Vector receiveDefinitions(String[] databases, String word) throws
	IllegalArgumentException, ConnectException {
		Vector result = new Vector();
		String buf;

		word = removeQuotationMarks(word);
		for (int i = 0; i < databases.length; i++) {
			send(DEFINE_COMMAND + " " + databases[i] + " \"" + word + "\"");
			buf = receive();
			if (buf == null) {
				connect();
				return receiveDefinitions(databases, word);
			} else if (buf.startsWith(NO_MATCH_RESPONSE)) {
				continue;
			} else if (buf.startsWith(DEFINITIONS_FOUND_RESPONSE)) {
				for (buf = receive(); 
				!buf.startsWith(COMMAND_COMPLETED_RESPONSE); 
				buf = receive()) {
					StringBuffer def = new StringBuffer();
					Definition completeDef;
					String returnedWord, dict, description;
					buf = buf.substring(buf.indexOf("\"") + 1).trim();
					returnedWord = buf.substring(0, buf.indexOf("\"")).trim();
					buf = buf.substring(buf.indexOf("\"") + 1).trim();
					dict = buf.substring(0, buf.indexOf(" ")).trim();
					buf = buf.substring(buf.indexOf(" ")).trim();
					description = buf.trim();
					for (buf = receive(); !buf.startsWith(".") || 
					buf.startsWith(".."); buf = receive()) {
						if (buf.startsWith("..")) {
							buf = buf.substring(1);
						}
						def.append(buf + "\n");
					}
					def = removeEndNewlines(def);
					completeDef = new Definition(dict, description, 
							returnedWord, def.toString());
					result.add(completeDef);
				}
			} else if (buf.startsWith(INVALID_DATABASE_RESPONSE)) {
				throw new IllegalArgumentException("Invalid database: " + 
						databases[i]);
			} else {
				throw new ConnectException(
						"Error connecting to server, following response received: " 
						+ buf);
			}
		}
		return result;
	}

	private String[][] receiveMatches(String[] databases, String strategy, 
			String word) throws IllegalArgumentException, ConnectException {
		String[][] finalResult = new String[0][2];
		String[][] subResult = new String[0][2];
		String buf;

		word = removeQuotationMarks(word);
		for (int i = 0; i < databases.length; i++) {
			send(MATCH_COMMAND + " " + databases[i] + " " + strategy + " \"" + 
					word + "\"");
			buf = receive();
			if (buf == null) {
				connect();
				return receiveMatches(databases, strategy, word);
			} else if (buf.startsWith(MATCHES_FOUND_RESPONSE)) {
				buf = buf.substring(buf.indexOf(" ")).trim();
				int numMatches = Integer.parseInt(buf.substring(0, 
						buf.indexOf(" ")));
				subResult = new String[numMatches][2];
				for (int j = 0; j < numMatches; j++) {
					buf = receive();
					subResult[j][0] = buf.substring(0, buf.indexOf(" "));
					subResult[j][1] = buf.substring(buf.indexOf(" ")).trim();
				}

				// next line should be "." to signify end
				buf = receive();
				if (!buf.equals(".")) {
					throw new RuntimeException("Supposed to be " + numMatches + 
					" matches only, but server returns more");
				}

				// next line should be return code 250
				buf = receive();

			} else if (buf.startsWith(NO_MATCH_RESPONSE)) {
				continue;
			} else if (buf.startsWith(INVALID_DATABASE_RESPONSE)) {
				throw new IllegalArgumentException("Invalid database: " + 
						databases[i]);
			} else if (buf.startsWith(INVALID_STRATEGY_REPONSE)) {
				throw new IllegalArgumentException("Invalid strategy: " + 
						strategy);
			} else {
				throw new ConnectException(
						"Error connecting to server, following response received: "
						+ buf);
			}

			// add new results to the existing ones
			int totalMatches = finalResult.length + subResult.length;
			String[][] tempResult = new String[totalMatches][2];
			for (int j = 0; j < finalResult.length; j++) {
				tempResult[j] = finalResult[j];
			}
			for (int j = 0; j < subResult.length; j++) {
				tempResult[finalResult.length + j] = subResult[j];
			}
			finalResult = tempResult;
		}
		return finalResult;
	}

	private String receiveDBInfo(String database) throws ConnectException, 
	IllegalArgumentException {
		String buf;
		String result = "";

		send(SHOW_INFO_COMMAND + " " + database);
		buf = receive();
		if (buf == null) {
			connect();
			return receiveDBInfo(database);
		} else if (buf.startsWith(DATABASE_INFORMATION_RESPONSE)) {
			buf = receive();
			while (!buf.equals(".")) {
				result += buf + "\n";
				buf = receive();
			}
			receive();
			return removeEndNewlines(new StringBuffer(result)).toString();
		} else if (buf.startsWith(INVALID_DATABASE_RESPONSE)) {
			throw new IllegalArgumentException("Invalid database: " + database);
		} else {
			throw new ConnectException(
					"Error connecting to server, following response received: " 
					+ buf);
		}
	}

	private  String[][] receiveDatabases() throws ConnectException {
		String responseCode;
		String buf;
		Vector databasesVector = new Vector();
		String[][] databasesArray;

		send(SHOW_DATABASES_COMMAND);
		responseCode = receive();
		if (responseCode == null) {
			connect();
			return receiveDatabases();
		} else if (responseCode.startsWith(DATABASES_AVAILABLE_RESPONSE)) {
			buf = receive();
			while (!buf.equals(".")) {
				databasesVector.add(buf);
				buf = receive();
			}
			databasesArray = new String[databasesVector.size()][2];
			for (int i = 0; i < databasesVector.size(); i++) {
				int spacePosition;
				buf = (String) databasesVector.get(i);
				spacePosition = buf.indexOf(" ");
				databasesArray[i][0] = buf.substring(0, spacePosition);
				databasesArray[i][1] = buf.substring(spacePosition).trim();
			}
			receive();
			return databasesArray;
		} else if (responseCode.startsWith(
				NO_DATABASES_AVAILABLE_RESPONSE)) {
			return new String[0][2];
		} else {
			throw new ConnectException(
					"Error connecting to server, following response received: "
					+ responseCode);
		}
	}

	private String[][] receiveStrategies() throws ConnectException {
		String[][] result;
		Vector stratVector = new Vector();
		Enumeration enum_ = stratVector.elements();
		String buf;

		send(SHOW_STRATEGIES_COMMAND);
		buf = receive();
		if (buf == null) {
			connect();
			return receiveStrategies();
		} else if (buf.startsWith(STRATEGIES_AVAILABLE_RESPONSE)) {
			buf = receive();
			while(!buf.equals(".")) {
				String strategy = buf.substring(0, buf.indexOf(" "));
				String description = buf.substring(buf.indexOf(" ") + 1);
				stratVector.add(strategy);
				stratVector.add(description);
				buf = receive();
			}
			result = new String[stratVector.size() / 2][2];
			for (int i = 0; i < result.length; i++) {
				result[i][0] = (String) enum_.nextElement();
				result[i][1] = (String) enum_.nextElement();
			}
			receive();	// this should be the code 250
			return result;
		} else if (buf.startsWith(NO_STRATEGIES_AVAILABLE_RESPONSE)) {
			return new String[0][2];
		} else {
			throw new ConnectException(
					"Error connecting to server, following response received: "
					+ buf);
		}
	}

	private String receiveServerInfo() throws ConnectException {
		String buf;
		StringBuffer result = new StringBuffer();

		send(SHOW_SERVER_INFO_COMMAND);
		buf = receive();
		if (buf == null) {
			connect();
			return receiveServerInfo();
		} else if (buf.startsWith(SERVER_INFORMATION_RESPONSE)) {
			while (!(buf = receive()).equals(".")) {
				result.append(buf + "\n");
			}
			// this should be return code 250
			buf = receive();
			return removeEndNewlines(result).toString();
		} else {
			throw new ConnectException(
					"Error connecting to server, following response received: " 
					+ buf);
		}
	}

	// removes any multiple newlines or spaces at the end of String, leaving back max one.
	private StringBuffer removeEndNewlines( StringBuffer text ) {
		int length = text.length();
		char c;

		if (length >= 1 && ((c = text.charAt(length - 1)) == '\n' || c == ' ')) {
			while (length >= 2 && ((c = text.charAt(length - 2)) == '\n' || c == ' ')) {
				text.deleteCharAt(length - 1);
				length--;
			}
		}

		return text;
	}

	//  removes leading and trailing quotation marks, if any
	private String removeQuotationMarks(String text) {
		text = text.trim();
		int length = text.length();
		if (text.charAt(0) == '\"' && text.charAt(length - 1) == '\"') {
			text = text.substring(1, length - 1);
		}
		return text;
	}
}
