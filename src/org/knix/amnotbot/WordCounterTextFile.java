package org.knix.amnotbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WordCounterTextFile implements WordCounter {
	
	Hashtable<String, Integer> htWords;	
	HashSet<String> ignoreWords;
	String word_log_file;
	
	public WordCounterTextFile(String ignoreWordsFilename, String word_log_file)
	{
		this.htWords = new Hashtable<String, Integer>();
		this.ignoreWords = new HashSet<String>();
		
		this.loadIgnoreWords(ignoreWordsFilename); 
		this.word_log_file = word_log_file;
	}
	
	private void loadIgnoreWords(String ignoreWordsFilename)
	{
		File aFile = new File(ignoreWordsFilename);
		BufferedReader input = null;
		
		try {
			input = new BufferedReader( new FileReader(aFile) );
			String line = null; //not declared within while loop
		   
			while (( line = input.readLine()) != null) {
				String [] words = line.split(" ");
				
				for (int i = 0; i < words.length; ++i) {		
//					String w = words[i].replaceAll("(\\.)+", " ");
					
					this.ignoreWords.add(words[i].toLowerCase().trim());
				}
								
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
		          //flush and close both "input" and its underlying FileReader
					input.close();				
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void buildWordsList(String nicks)
	{
		Pattern messagePattern = Pattern.compile("(\\[.*\\]\\s\\w*>)(.*)");
		Pattern botPattern = Pattern.compile("(\\[.*\\]\\s" + BotConstants.getBotConstants().getNick() + ".*>)(.*)");
		Pattern urlPattern = 
			Pattern.compile("(\\[.*\\]\\s\\w*>\\s)"
					+ "((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)",
					Pattern.CASE_INSENSITIVE);
		Pattern commandPattern = Pattern.compile("(\\[.*\\]\\s\\w*>\\s)(!\\w*\\s)(.*)");
		Pattern nickPattern = null;
		Matcher m;
		
		BotLogger.getDebugLogger().debug("botPattern " + botPattern.pattern());
		BotLogger.getDebugLogger().debug("urlPattern " + urlPattern.pattern());
		BotLogger.getDebugLogger().debug("commandPattern " + commandPattern.pattern());
		
		if (nicks != null) {			
			nickPattern = Pattern.compile(this.buildNickPattern(nicks), Pattern.CASE_INSENSITIVE);
			BotLogger.getDebugLogger().debug("commandPattern " + nickPattern.pattern());
		}
				
		try {
//			File aFile = new File(word_log_file);
			FileInputStream aFile = new FileInputStream(word_log_file);

			BotLogger.getDebugLogger().debug(word_log_file);
			
			BufferedReader input = null;
			input = new BufferedReader( new InputStreamReader(aFile, "US-ASCII") );
	       	  	    
			String line = null; //not declared within while loop		      
			while (( line = input.readLine()) != null) {
				if (nickPattern != null) {
					if (!nickPattern.matcher(line).matches())
						continue;
				}

				if (botPattern.matcher(line).matches())
					continue;
				
				if (urlPattern.matcher(line).matches())
					continue;

				if (commandPattern.matcher(line).matches())
					continue;
				
				m = messagePattern.matcher(line);
				if (m.matches())
					this.countWordsInLine(m.group(2));
			}

			input.close();

		} catch (IOException e) {
			BotLogger.getDebugLogger().debug("Exception IO buildWordsList()");
			BotLogger.getDebugLogger().debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private String buildNickPattern(String nicklist)
	{
		String nickPattern = new String();
	 
		BotLogger.getDebugLogger().debug(":" + nicklist + ":");
		if (nicklist == null)
			return null;
		
		String [] nicks = nicklist.split(" ");
		
		BotLogger.getDebugLogger().debug(nicks.length);
		nickPattern = "\\[.*\\]\\s(";
		for (int i = 0; i < nicks.length; ++i) {
			if (i > 0)
				nickPattern += "|";
			nickPattern += nicks[i];
		}
		nickPattern += ")>(.*)";
		
		return nickPattern;
	}
			
	private void countWordsInLine(String line)
	{
		String [] words = line.split(" ");
		
		for (int i = 0; i < words.length; ++i) {
//			String w = words[i].replaceAll("(\\.)+", " ");
			String w = words[i];

			if (w.matches("\\w*\\p{Punct}"))
				w = w.substring(0, w.length() - 1);
			if (w.matches("\\p{Punct}" + "\\w*"))
				w = w.substring(1, w.length());
			
			w = w.toLowerCase().trim();
			if (this.ignoreWords.contains(w))
				continue;
			
			if (w.length() < 4)
				continue;
			
			Integer n = (Integer)htWords.get(w);
			if (n != null) {
				htWords.put(w, new Integer(n.intValue() + 1));
				//BotLogger.getDebugLogger().debug("word = " + words[i] + " n = " + n.intValue() + 1);
			} else
				htWords.put(w, 1);
		}			
	}
	
	public void getContents(String wordsFilename) 
	{			
		File aFile = new File(wordsFilename);
		BufferedReader input = null;
		
		try {
			input = new BufferedReader( new FileReader(aFile) );
			String line = null; //not declared within while loop		      
			while (( line = input.readLine()) != null) {
				this.countWordsInLine(line);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
		          //flush and close both "input" and its underlying FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String mostUsedWords(int numberOfWords, String nickList, String ignoreDate)
	{
		WordComparator<WordNumber> c = new WordComparator<WordNumber>();
		PriorityQueue<WordNumber> wordsCounted = new PriorityQueue<WordNumber>(this.htWords.size() + 10, c);
		
		this.buildWordsList(nickList);
		
		String word;
		for (Enumeration<String> words = this.htWords.keys(); words.hasMoreElements(); ) {
			word = words.nextElement();
			wordsCounted.add(new WordNumber(word, this.htWords.get(word).intValue()));			
		}
		
		String wordsSorted = "";		
		if (!wordsCounted.isEmpty()) {
			numberOfWords = 
				numberOfWords > wordsCounted.size() ? wordsCounted.size() : numberOfWords;
			for (int i = 0; i < numberOfWords; ++i) {
				WordNumber w = (WordNumber)wordsCounted.poll();
				wordsSorted += w.word() + " " + "(" + w.number() + ")" + " ";
			}
		}
		
		return wordsSorted;
	}	
	
	public String mostUsedWordsBy(int numberOfWords, String word, String date)
	{
		String val = "Not yet implemented";
		
		return val;
	}
	
	public String topLines(int numberOfLines, String date)
	{
		String val = "Not yet implemented";
		
		return val;
	}
	
	public String avgWordsLine(int numberOfusers, String nickList, String date)
	{
		String val = "Not yet implemented";
		
		return val;
	}
}


class WordNumber 
{
	private int number;
	private String word;
	
	public WordNumber(String word, int number)
	{
		this.word = word;
		this.number = number;
	}
	
	public String word()
	{
		return this.word;
	}
	
	public int number()
	{
		return this.number;
	}
}

class WordComparator<T> implements Comparator<T>
{
	
	public WordComparator()
	{
	}

	public int compare(T arg0, T arg1) 
	{		
		WordNumber w0, w1;
		w0 = (WordNumber)arg0;
		w1 = (WordNumber)arg1;
		
		if (w0.number() > w1.number()) {
			return -1;
		} else if (w0.number() < w1.number()) {
			return 1;
		}
		
		return 0;
	}	 	
}
