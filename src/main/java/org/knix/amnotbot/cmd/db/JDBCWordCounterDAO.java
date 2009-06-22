package org.knix.amnotbot.cmd.db;

import org.knix.amnotbot.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.lang.StringUtils;

public class JDBCWordCounterDAO implements WordCounterDAO
{
    private Connection connection = null;

    public JDBCWordCounterDAO(String db) throws SQLException
    {
        this.connection = BotDBFactory.instance().getConnection(db);
    }

    private String getNickWhereClause(String [] nicks)
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
        ResultSet rs = null;
        String result = null;
        Statement statement = null;
        try {            
            statement = this.connection.createStatement();
            rs = statement.executeQuery(query);
            result = this.buildResult(rs);
            statement.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e);
            BotLogger.getDebugLogger().debug(e);
        }
        return result;
    }

    private String buildResult(ResultSet rs) throws SQLException
    {
        if (rs == null) return null;

        String result = new String();        
        while (rs.next()) {           
            rs.getString(1);
            float fv = Float.valueOf(rs.getString(2));
            int iv = Math.round(Float.valueOf( rs.getString(2) ));

            String out;
            float rv = fv - iv;
            if (rv != 0) {
                // Obtain a number with just two decimals
                out = String.valueOf(Math.round(
                        Float.valueOf(rs.getString(2)) * 100.0) / 100.0);
            } else {
                out = String.valueOf(
                        Math.round(Float.valueOf(rs.getString(2))));
            }
            result += " " + rs.getString(1);
            result += "(" + out + ")";
        }
        return result.trim();
    }    

    public String mostUsedWords(int numberOfWords, String [] nicks, String date)
    {
        String where = null;
        if (nicks[0] != null) {
            where = this.getNickWhereClause(nicks);
        }

        if (!StringUtils.isBlank(date)) {
            if (where != null) {
                where += " AND ";
                where += this.getDateWhereClause(date);
            } else {
                where = this.getDateWhereClause(date);
            }
        }

        String query;
        if (where == null) {
            query = "SELECT word, SUM(repetitions) AS rep FROM datewordsnick " +
                    " GROUP BY word ORDER BY rep DESC LIMIT " +                 
                    Integer.toString(numberOfWords);
        } else {
            query = "SELECT word, SUM(repetitions) AS rep FROM datewordsnick " +
                    "WHERE " + where + " GROUP BY word ORDER BY rep " +
                    "DESC LIMIT " +
                    Integer.toString(numberOfWords);
        }
        return this.runQuery(query);
    }

    public String mostUsedWordsBy(int numberOfWords, 
            String [] words,
            String date)
    {
        String where = null;
        if (words[0] != null) {
            where = this.getWordsWhereClause(words);
        }

        if (!StringUtils.isBlank(date)) {
            where += " AND " + this.getDateWhereClause(date);
        }

        String query;
        if (where == null) {
            query = "SELECT nick, SUM(repetitions) AS rep FROM datewordsnick " +
                    "GROUP BY nick ORDER BY rep DESC LIMIT " +
                    Integer.toString(numberOfWords);
        } else {
            query = "SELECT nick, SUM(repetitions) AS rep FROM datewordsnick " +
                    "WHERE " + where + " GROUP BY nick ORDER BY rep " +
                    "DESC LIMIT " +
                    Integer.toString(numberOfWords);
        }
        return this.runQuery(query);
    }

    public String topLines(int numberOfusers, String date)
    {
        String where = null;
        if (!StringUtils.isBlank(date)) {
            where = this.getDateWhereClause(date);
        }

        String query;
        if (where == null) {
            query = "SELECT nick, SUM(repetitions) AS rep FROM lines " +
                    "GROUP BY nick ORDER BY rep DESC LIMIT " +
                    Integer.toString(numberOfusers);
        } else {
            query = "SELECT nick, SUM(repetitions) AS rep FROM lines WHERE " +
                    where + " GROUP BY nick ORDER BY rep " +
                    "DESC LIMIT " +
                    Integer.toString(numberOfusers);
        }
        return this.runQuery(query);
    }

    public String avgWordsLine(int numberOfusers, String [] nicks, String date)
    {
        String where = null;
        if (nicks[0] != null) {
            where = this.getNickWhereClause(nicks);
        }

        if (!StringUtils.isBlank(date)) {
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
                    "WHERE n1 = n2 GROUP BY n1, n2, rep3 ORDER BY rep3 " +
                    "DESC LIMIT " +
                    Integer.toString(numberOfusers);
        } else {
            query = "SELECT n1, (rep1/rep2) as rep3 FROM " +
                    "(SELECT nick AS n1, SUM(repetitions) AS rep1" +
                    " FROM datewordsnick WHERE " + where + " GROUP BY n1)," +
                    "(SELECT nick AS n2, SUM(repetitions) AS rep2" +
                    " FROM lines WHERE " + where + " GROUP BY n2) " +
                    "WHERE n1 = n2 GROUP BY n1, n2, rep3 ORDER BY rep3 " +
                    "DESC LIMIT " +
                    Integer.toString(numberOfusers);
        }
        return this.runQuery(query);
    }
}
