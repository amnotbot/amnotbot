package org.knix.amnotbot.command;

public interface WordCounter
{

    public abstract String mostUsedWords(int numberOfWords, String [] nicks,
            String date);

    public abstract String mostUsedWordsBy(int numberOfWords, String [] words,
            String date);

    public abstract String topLines(int numberOfUsers, String date);

    public abstract String avgWordsLine(int numberOfusers, String [] nicks,
            String date);
}