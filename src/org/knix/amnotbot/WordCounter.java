package org.knix.amnotbot;

public interface WordCounter {

	public abstract String mostUsedWords(int numberOfWords, 
			String nickList, String date);
	
	public abstract String mostUsedWordsBy(int numberOfWords, 
			String word, String date);
	
	public abstract String topLines(int numberOfUsers, String date);
	
	public abstract String avgWordsLine(int numberOfusers, String nickList, String date);
}