package org.knix.amnotbot;

import SQLite.Database;
import SQLite.Exception;

public class WordCounterSqlite implements WordCounter {
	
	private Database db;
	private String db_filename;
	
	public WordCounterSqlite(String word_log_file)
	{
		this.db_filename = word_log_file + ".db";
		this.db = new Database();

		System.out.println("Querying db: " + this.db_filename);
	}
	
	public String getNickList(String nickList)
	{
		if (nickList == null)
			return null;
		
		String [] nicks = nickList.split(" ");
		String where = "";
		
		if (nicks.length == 0)
			return null;
		
		for (int i = 0; i < nicks.length; ++i) {
			if (i > 0)
				where += " OR ";
			where += "nick = '" + nicks[i] + "'";								
		}
		
		return where;
	}
	
	private String getDate(String date)
	{
		char c = date.charAt(0);
		int sub = 1;
		if (c != '<' && c != '>' && c != '=') {
			c = '=';
			sub = 0;
		}

		String where = " d " + String.valueOf(c) + " '" + date.substring(sub) + "'";
		
		return where;
	}
	
	private String getWords(String words)
	{
		String [] w = words.split(" ");
		String where = "";
		
		for (int i = 0; i < w.length; ++i) {
			if (i > 0)
				where += " OR ";
			where += "word = '" + w[i] + "'";								
		}
		
		return where;
	}
	
	private String runQuery(String query)
	{
		try {
			this.db.open(this.db_filename, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MostUsedWordsTableFmt table = new MostUsedWordsTableFmt();
		try {
			this.db.exec(query, table);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
		    this.db.close();
		} catch (Exception ex) {
		    System.err.println(ex.getMessage());
		}
		
		System.out.println(table.getResults());
		return table.getResults();		
	}
			
	public String mostUsedWords(int numberOfWords, String nickList, String date) 
	{							
		String where = null;
		if (nickList != null)
			where = this.getNickList(nickList);
		
		if (date != null) {
			if (where != null) {
				where += " AND ";
				where += this.getDate(date);
			} else {
				where = this.getDate(date);
			}
		}
			
		System.out.println("date=" + date);

		String query;
		if (where == null) {
			query = "SELECT word, SUM(repetitions) AS rep FROM datewordsnick"
					+ " GROUP BY word ORDER BY rep COLLATE BINARY DESC LIMIT " 
					+ Integer.toString(numberOfWords);
		} else {							
			query = "SELECT word, SUM(repetitions) AS rep FROM datewordsnick WHERE " 
				+ where
				+ " GROUP BY word ORDER BY rep COLLATE BINARY DESC LIMIT " 
				+ Integer.toString(numberOfWords);
		}
			
		System.out.println("nicklist =" + nickList);
		System.out.println("query = " + query);
		System.out.println("where = " + where);
		
		return this.runQuery(query);
	}
	
	public String mostUsedWordsBy(int numberOfWords, String words, String date) 
	{
		String where = null;

		System.out.println(words);
		System.out.println(words.length());
		
		if (words.compareTo("*") != 0)			
			where = this.getWords(words);
								
		if (date != null)
			where += " AND " + this.getDate(date);
		
		String query;
		if (where == null) {
			query = "SELECT nick, SUM(repetitions) AS rep FROM datewordsnick "
					+ " GROUP BY nick ORDER BY rep COLLATE BINARY DESC LIMIT "
					+ Integer.toString(numberOfWords);
		} else {
			query = "SELECT nick, SUM(repetitions) AS rep FROM datewordsnick WHERE "
					+ where
					+ " GROUP BY nick ORDER BY rep COLLATE BINARY DESC LIMIT "
					+ Integer.toString(numberOfWords);
		}
		
		System.out.println("date =" + date);
		System.out.println("query = " + query);
		System.out.println("where = " + where);
		
		return this.runQuery(query);	
	}
	
	public String topLines(int numberOfusers, String date)
	{
		String where = null;
				
		if (date != null)
			where = this.getDate(date);			
			
		System.out.println("date=" + date);

		String query;
		if (where == null) {
			query = "SELECT nick, SUM(repetitions) AS rep FROM lines"
					+ " GROUP BY nick ORDER BY rep COLLATE BINARY DESC LIMIT " 
					+ Integer.toString(numberOfusers);
		} else {							
			query = "SELECT nick, SUM(repetitions) AS rep FROM lines WHERE " 
				+ where
				+ " GROUP BY nick ORDER BY rep COLLATE BINARY DESC LIMIT " 
				+ Integer.toString(numberOfusers);
		}
			
		System.out.println("query = " + query);
		System.out.println("where = " + where);
		
		return this.runQuery(query);
	}
	
	public String avgWordsLine(int numberOfusers, String nickList, String date)
	{
		String where = null;
		
		if (nickList != null)
			where = this.getNickList(nickList);
				
		if (date != null) {
			if (where != null)
				where += " AND " + this.getDate(date);
			else
				where = this.getDate(date);
		}
			
		System.out.println("date=" + date);

		String query;
		if (where == null) {
			query = "SELECT n1, (rep1/rep2) as rep3 FROM " 
					+ "(SELECT nick AS n1, SUM(repetitions) AS rep1 FROM datewordsnick GROUP BY n1),"
					+ "(SELECT nick AS n2, SUM(repetitions) AS rep2 FROM lines GROUP BY n2)"
					+ " WHERE n1 = n2 GROUP BY n1, n2 ORDER BY rep3 COLLATE BINARY DESC LIMIT "
					+ Integer.toString(numberOfusers);					
		} else {							
			query = "SELECT n1, (rep1/rep2) as rep3 FROM " 
				+ "(SELECT nick AS n1, SUM(repetitions) AS rep1 FROM datewordsnick WHERE "
				+ where
				+ " GROUP BY n1),"
				+ "(SELECT nick AS n2, SUM(repetitions) AS rep2 FROM lines WHERE "
				+ where
				+ " GROUP BY n2)"
				+ " WHERE n1 = n2 "
				+ " GROUP BY n1, n2 ORDER BY rep3 COLLATE BINARY DESC LIMIT " 
				+ Integer.toString(numberOfusers);
		}
		
		System.out.println("nickList = " + nickList);
		System.out.println("date = " + date);
		System.out.println("query = " + query);
		System.out.println("where = " + where);
		
		return this.runQuery(query);
	}

}

class MostUsedWordsTableFmt implements SQLite.Callback
{
	String result;
	
	public MostUsedWordsTableFmt()
	{
		this.result = new String();
	}

	public void columns(String[] arg0) 
	{		
	}

	public boolean newrow(String[] arg0) 
	{
		float fv = Float.valueOf(arg0[1]);
		int iv = Math.round(Float.valueOf(arg0[1]));

		float rv = fv - iv;
		String out;
		if (rv != 0)
			out = String.valueOf( Math.round(Float.valueOf(arg0[1]) * 100.0) / 100.0 );
		else
			out = String.valueOf( Math.round(Float.valueOf(arg0[1])) );
		
		this.result += " " + arg0[0];
		this.result += "(" + out + ")";
		
		return false;
	}

	public void types(String[] arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public String getResults()
	{
		return this.result;
	}
}
