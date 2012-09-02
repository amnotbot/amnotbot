package com.github.amnotbot.task.stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * Recreates and updates the database tables holding statistics information
 * about the irc channel.
 * @author gpoppino
 */
public class StatsGenerator
{

    private Properties p;
    private HashSet<String> ignoreWords;
    private StatsTableManager tableManager;

    /**
     *
     * @param p Properties that hold different options used to generate the
     * statistics.
     */
    public StatsGenerator(Properties p)
    {
        this.p = p;
        this.ignoreWords = new HashSet<String>();
        this.tableManager = new StatsTableManager(p.getProperty("backend"));

        this.loadIgnoreWords(p.getProperty("ignorefile"));
    }

    /**
     * Regenerates the statistics for an irc log file.
     * @param logFilename File name containing irc channel information.
     * @throws SQLException
     * @throws ParseException
     */
    public void regenerate(String logFilename)
            throws SQLException, ParseException
    {
        this.tableManager.init(logFilename + ".db");
        
        this.buildWordsList(logFilename, null);

        this.tableManager.close();
    }

    private void loadIgnoreWords(String ignoreWordsFilename)
    {
        BufferedReader input = null;
        File aFile;
        try {
            aFile = new File(ignoreWordsFilename);
            input = new BufferedReader(new FileReader(aFile));
            String line = null;
            while ((line = input.readLine()) != null) {
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; ++i) {
                    this.ignoreWords.add(words[i].toLowerCase().trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
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

    private void buildWordsList(String logFilename, String[] nicks)
            throws SQLException, ParseException
    {
        Pattern botPattern;
        Pattern urlPattern;
        Pattern nickPattern;
        Pattern msgPattern, msgPattern2;
        Pattern commandPattern;

        nickPattern = null;
        botPattern = Pattern.compile("(\\[.*\\]\\s.*" +
                this.p.getProperty("botnick") + ">)\\s(.*)");
        urlPattern = Pattern.compile("(\\[.*\\]\\s\\w*>\\s)" +
                "((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)",
                Pattern.CASE_INSENSITIVE);
        msgPattern = Pattern.compile("\\[([^>]*)\\]\\s(\\w*)>\\s(.*)");
        msgPattern2 = Pattern.compile(
                "\\[([^>]*)\\]\\s\\[[^>]*\\]\\s<.?(\\w*)>\\s(.*)");
        commandPattern = Pattern.compile(
                "(\\[.*\\]\\s.*>\\s)\\" + 
                this.p.getProperty("cmdtrigger") + "\\w\\s?.*");

        if (nicks != null) {
            nickPattern = Pattern.compile(this.buildNickPattern(nicks),
                    Pattern.CASE_INSENSITIVE);
        }

        try {
            StatsProgress progressBar = 
                    new StatsProgress(logFilename, 
                            new File(logFilename).length());
            FileInputStream aFile = new FileInputStream(logFilename);

            BufferedReader input = null;
            input = new BufferedReader(
                    new InputStreamReader(aFile, "UTF-8"));

            String line = null;
            Matcher m1, m2;
            long progress = 0;
            while ((line = input.readLine()) != null) {
                progress += line.length();
                progressBar.showProgress(progress);
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
                m1 = msgPattern.matcher(line);
                if (m1.matches()) {
                    this.countWordsInLine(this.getDate( m1.group(1) ),
                            this.normalizeNick( m1.group(2) ), m1.group(3));
                    continue;
                }                
                m2 = msgPattern2.matcher(line);
                if (m2.matches()) {
                    this.countWordsInLine(this.getDate( m2.group(1) ),
                            this.normalizeNick( m2.group(2) ), m2.group(3));
                }
            }
            input.close();
            progressBar.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    private void countWordsInLine(Date date, String nick, String line)
            throws SQLException
    {
        String[] words = line.split(" ");
        int minWordLength = 
                Integer.parseInt( this.p.getProperty("minwordlen") );
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

            if (w.length() < minWordLength) continue;

            this.tableManager.update(new StatsRecordEntity(date, nick, w));
        }
        this.tableManager.update(new StatsRecordEntity(date, nick, null));
    }

    private String normalizeNick(String nick)
    {
        if (!nick.endsWith("_")) return nick;

        String n;
        n = nick = nick.trim();
        while (true) {
            nick = StringUtils.removeEnd(nick, "_");
            if (nick.equals(n)) break;
            n = nick;
        }
        return nick;
    }

    private String buildNickPattern(String[] nicks)
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

    private Date getDate(String date) throws ParseException
    {
        Pattern formatOne, formatTwo, formatThree;

        // Tue Aug 22 2006
        formatOne = Pattern.compile("[a-zA-Z]{3}\\s([a-zA-Z]{3}\\s[0-9]{2}" +
                "\\s[0-9]{4})");
        // 2008-09-25 01:06:40,653
        formatTwo = Pattern.compile("([0-9]{4}\\-[0-9]{2}\\-[0-9]{2})" +
                "\\s[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}\\,[0-9]{3}");
        // 03/21/2008 11:40:03
        formatThree = Pattern.compile("([0-9]{2}\\/[0-9]{2}\\/[0-9]{4})" +
                "\\s[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}");

        Matcher m;
        Date rDate = null;
                
        m = formatOne.matcher(date);
        if (m.matches()) {
            String[] d;
            d = m.group(1).split(" ");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
            rDate = sdf.parse(d[2] + "-" + d[0] + "-" + d[1]);
            return rDate;
        }

        m = formatTwo.matcher(date);
        if (m.matches()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            rDate = sdf.parse( m.group(1) );
            return rDate;
        }

        m = formatThree.matcher(date);
        if (m.matches()) {
            String[] d;
            d = m.group(1).split("/");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            rDate = sdf.parse(d[2] + "-" + d[0] + "-" + d[1]);
            return rDate;
        }

        return rDate;
    }
}
