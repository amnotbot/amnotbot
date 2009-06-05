package org.knix.amnotbot.cmd;

import org.knix.amnotbot.*;
import SQLite.Database;
import SQLite.Exception;

public class WordCounterSqlite implements WordCounter
{

    private Database db;
    private String db_filename;

    public WordCounterSqlite(String word_log_file)
    {
        this.db_filename = word_log_file + ".db";
        this.db = new Database();
    }

    public String getNickWhereClause(String [] nicks)
    {       
        if (nicks.length == 0) return null;

        String where = "";
        for (int i = 0; i < nicks.length; ++i) {
            if (i > 0) {
                where += " OR ";
            }
            where += "nick = '" + nicks[i] + "'";
        }
        return where;
    }

    private String getDateWhereClause(String date)
    {
        int sub = 1;
        char c = date.charAt(0);
        if (c != '<' && c != '>' && c != '=') {
            c = '=';
            sub = 0;
        }
        String where;
        where = " d " + String.valueOf(c) + " '" + date.substring(sub) + "'";
        return where;
    }

    private String getWordsWhereClause(String [] words)
    {
        String where = "";
        for (int i = 0; i < words.length; ++i) {
            if (i > 0) {
                where += " OR ";
            }
            where += "word = '" + words[i] + "'";
        }
        return where;
    }

    private String runQuery(String query)
    {
        try {
            this.db.open(this.db_filename, 0);
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
            return "";  // zero words
        }

        MostUsedWordsTableFmt table = new MostUsedWordsTableFmt();
        try {
            this.db.exec(query, table);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        try {
            this.db.close();
        } catch (Exception ex) {
            BotLogger.getDebugLogger().debug(ex.getMessage());
            return "";
        }
        return table.getResults();
    }

    public String mostUsedWords(int numberOfWords, String [] nicks, String date)
    {
        String where = null;
        if (nicks != null) {
            where = this.getNickWhereClause(nicks);
        }

        if (date != null) {
            if (where != null) {
                where += " AND ";
                where += this.getDateWhereClause(date);
            } else {
                where = this.getDateWhereClause(date);
            }
        }

        String query;
        if (where == null) {
            query = "SELECT word, SUM(repetitions) AS rep FROM datewordsnick" +
                    " GROUP BY word ORDER BY rep COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfWords);
        } else {
            query = "SELECT word, SUM(repetitions) AS rep FROM datewordsnick " +
                    "WHERE " + where + " GROUP BY word ORDER BY rep " +
                    "COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfWords);
        }
        return this.runQuery(query);
    }

    public String mostUsedWordsBy(int numberOfWords, 
            String [] words,
            String date)
    {
        String where = null;
        if (words != null) {
            where = this.getWordsWhereClause(words);
        }

        if (date != null) {
            where += " AND " + this.getDateWhereClause(date);
        }

        String query;
        if (where == null) {
            query = "SELECT nick, SUM(repetitions) AS rep FROM datewordsnick " +
                    "GROUP BY nick ORDER BY rep COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfWords);
        } else {
            query = "SELECT nick, SUM(repetitions) AS rep FROM datewordsnick " +
                    "WHERE " + where + " GROUP BY nick ORDER BY rep " +
                    "COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfWords);
        }
        return this.runQuery(query);
    }

    public String topLines(int numberOfusers, String date)
    {
        String where = null;
        if (date != null) {
            where = this.getDateWhereClause(date);
        }

        String query;
        if (where == null) {
            query = "SELECT nick, SUM(repetitions) AS rep FROM lines " +
                    "GROUP BY nick ORDER BY rep COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfusers);
        } else {
            query = "SELECT nick, SUM(repetitions) AS rep FROM lines WHERE " +
                    where + " GROUP BY nick ORDER BY rep " +
                    "COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfusers);
        }
        return this.runQuery(query);
    }

    public String avgWordsLine(int numberOfusers, String [] nicks, String date)
    {
        String where = null;
        if (nicks != null) {
            where = this.getNickWhereClause(nicks);
        }

        if (date != null) {
            if (where != null) {
                where += " AND " + this.getDateWhereClause(date);
            } else {
                where = this.getDateWhereClause(date);
            }
        }

        String query;
        if (where == null) {
            query = "SELECT n1, (rep1/rep2) as rep3 FROM " +
                    "(SELECT nick AS n1, SUM(repetitions) AS rep1" +
                    " FROM datewordsnick GROUP BY n1)," +
                    "(SELECT nick AS n2, SUM(repetitions) AS rep2" +
                    " FROM lines GROUP BY n2) " +
                    "WHERE n1 = n2 GROUP BY n1, n2 ORDER BY rep3 " +
                    "COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfusers);
        } else {
            query = "SELECT n1, (rep1/rep2) as rep3 FROM " +
                    "(SELECT nick AS n1, SUM(repetitions) AS rep1" +
                    " FROM datewordsnick WHERE " + where + " GROUP BY n1)," +
                    "(SELECT nick AS n2, SUM(repetitions) AS rep2" +
                    " FROM lines WHERE " + where + " GROUP BY n2) " +
                    "WHERE n1 = n2 GROUP BY n1, n2 ORDER BY rep3 " +
                    "COLLATE BINARY DESC LIMIT " +
                    Integer.toString(numberOfusers);
        }
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

        String out;
        float rv = fv - iv;        
        if (rv != 0) {
            // Obtain a number with just two decimals
            out = String.valueOf(Math.round(Float.valueOf(arg0[1]) * 100.0)
                    / 100.0);
        } else {
            out = String.valueOf(Math.round(Float.valueOf(arg0[1])));
        }
        this.result += " " + arg0[0];
        this.result += "(" + out + ")";
        return false;
    }

    public void types(String[] arg0)
    {
    }

    public String getResults()
    {
        return this.result;
    }
}
