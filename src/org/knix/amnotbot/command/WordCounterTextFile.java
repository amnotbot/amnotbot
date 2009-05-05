package org.knix.amnotbot.command;

import org.knix.amnotbot.*;
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


public class WordCounterTextFile implements WordCounter
{

    String log_filename;
    HashSet<String> ignoreWords;
    Hashtable<String, Integer> htWords;

    public WordCounterTextFile(String ignoreWordsFilename, String log_filename)
    {
        this.ignoreWords = new HashSet<String>();
        this.htWords = new Hashtable<String, Integer>();
        this.log_filename = log_filename;

        this.loadIgnoreWords(ignoreWordsFilename);
    }

    private void loadIgnoreWords(String ignoreWordsFilename)
    {
        BufferedReader input = null;
        File aFile = new File(ignoreWordsFilename);
        try {
            input = new BufferedReader(new FileReader(aFile));
            String line = null;
            while ((line = input.readLine()) != null) {
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; ++i) {
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
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildWordsList(String [] nicks)
    {
        Pattern botPattern;
        Pattern urlPattern;
        Pattern nickPattern;
        Pattern messagePattern;
        Pattern commandPattern;        

        nickPattern = null;        
        botPattern = Pattern.compile("(\\[.*\\]\\s" +
                BotConstants.getBotConstants().getNick() + ".*>)(.*)");
        urlPattern = Pattern.compile("(\\[.*\\]\\s\\w*>\\s)" +
                "((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)",
                Pattern.CASE_INSENSITIVE);
        messagePattern = Pattern.compile("(\\[.*\\]\\s\\w*>)(.*)");
        commandPattern = Pattern.compile("(\\[.*\\]\\s\\w*>\\s)(!\\w*\\s)(.*)");

        if (nicks != null) {
            nickPattern = Pattern.compile(this.buildNickPattern(nicks),
                    Pattern.CASE_INSENSITIVE);
        }

        try {
            FileInputStream aFile = new FileInputStream(log_filename);

            BufferedReader input = null;
            input = new BufferedReader(
                        new InputStreamReader(aFile, "US-ASCII")
                    );

            String line = null;
            Matcher m;
            while ((line = input.readLine()) != null) {
                // Skip unwanted lines.
                if (nickPattern != null) {
                    if (!nickPattern.matcher(line).matches()) {
                        continue;
                    }
                }
                if (botPattern.matcher(line).matches()) continue;

                if (urlPattern.matcher(line).matches()) continue;

                if (commandPattern.matcher(line).matches()) continue;

                // Count words in lines of interest.
                m = messagePattern.matcher(line);
                if (m.matches()) {
                    this.countWordsInLine(m.group(2));
                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
    }

    private String buildNickPattern(String [] nicks)
    {
        String nickPattern = new String();
               
        nickPattern = "\\[.*\\]\\s(";
        for (int i = 0; i < nicks.length; ++i) {
            if (i > 0) {
                nickPattern += "|";
            }
            nickPattern += nicks[i];
        }
        nickPattern += ")>(.*)";
        return nickPattern;
    }

    private void countWordsInLine(String line)
    {
        String[] words = line.split(" ");

        for (int i = 0; i < words.length; ++i) {
            String w = words[i];

            // remove dots from words
            if (w.matches("\\w*\\p{Punct}")) {
                w = w.substring(0, w.length() - 1);
            }
            if (w.matches("\\p{Punct}" + "\\w*")) {
                w = w.substring(1, w.length());
            }

            w = w.toLowerCase().trim();
            // skip words of no interest
            if (this.ignoreWords.contains(w)) continue;

            if (w.length() < 4) continue;

            Integer n = (Integer) this.htWords.get(w);
            if (n != null) {
                this.htWords.put(w, new Integer(n.intValue() + 1));
            } else {
                this.htWords.put(w, 1);
            }
        }
    }

    public String mostUsedWords(int n,
            String [] nicks,
            String ignoreDate)
    {
        WordComparator<WordNumber> c = new WordComparator<WordNumber>();
        PriorityQueue<WordNumber> wordsCounted =
                new PriorityQueue<WordNumber>(this.htWords.size() + 10, c);

        this.buildWordsList(nicks);

        String word;
        Enumeration<String> k;
        for (k = this.htWords.keys(); k.hasMoreElements();) {
            word = k.nextElement();
            wordsCounted.add(new WordNumber(word,
                    this.htWords.get(word).intValue()));
        }
        
        String topWords = "";
        if (!wordsCounted.isEmpty()) {
            n = n > wordsCounted.size() ? wordsCounted.size() : n;
            for (int i = 0; i < n; ++i) {
                WordNumber w = (WordNumber) wordsCounted.poll();
                topWords += w.word() + " " + "(" + w.number() + ")" + " ";
            }
        }
        return topWords;
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

    public String mostUsedWordsBy(int numberOfWords, String[] words, String date)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String avgWordsLine(int numberOfusers, String[] nicks, String date)
    {
        throw new UnsupportedOperationException("Not supported yet.");
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
        w0 = (WordNumber) arg0;
        w1 = (WordNumber) arg1;

        if (w0.number() > w1.number()) return -1;        
        if (w0.number() < w1.number()) return 1;
        return 0;
    }
}