package org.knix.amnotbot.cmd.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gpoppino
 */
public class HsqldbQuoteDAO implements QuoteDAO
{
    Connection conn = null;

    public HsqldbQuoteDAO()
    {
    }

    public HsqldbQuoteDAO(Connection conn) throws SQLException
    {
        this.conn = conn;
    }

    private ResultSet execQuery(String query) throws SQLException
    {
        Statement statement;
        ResultSet rs = null;

        statement = this.conn.createStatement();
        statement.setQueryTimeout(30);
        rs = statement.executeQuery(query);
     
        return rs;
    }

    public boolean save(QuoteEntity quote) throws SQLException
    {        
        String query;
        query = "INSERT INTO quotes (nick, desc) VALUES (" + "'" +
                quote.getUser() + "'" + ", " + "\'" + quote.getQuote() +
                "\'" + ");";

        int rowCount = -1;
        Statement statement;
        statement = this.conn.createStatement();
        rowCount = statement.executeUpdate(query);
        statement.close();

        return (rowCount > 0);
    }

    public boolean delete(int quoteId) throws SQLException
    {
        String query;

        query = "DELETE FROM quotes WHERE id=" + Integer.valueOf(quoteId);

        int rowCount = -1;
        Statement statement;
        statement = this.conn.createStatement();
        rowCount = statement.executeUpdate(query);
        statement.close();

        return (rowCount > 0);
    }

    public QuoteEntity findById(int quoteId) throws SQLException
    {
        ResultSet rs;
        QuoteEntity quote = new QuoteEntity();

        rs = this.execQuery("SELECT * FROM quotes WHERE id=" +
                Integer.valueOf(quoteId));

        if (rs.next()) {
            quote.setId( rs.getInt(1) );
            quote.setUser( rs.getString(2) );
            quote.setQuote( rs.getString(3) );
        }

        rs.close();

        return quote;
    }

    public QuoteEntity findRandom() throws SQLException
    {
        ResultSet rs;
        QuoteEntity quote = new QuoteEntity();

        rs = this.execQuery("SELECT * FROM quotes ORDER BY RAND()");

        if (rs.next()) {
            quote.setId( rs.getInt(1) );
            quote.setUser( rs.getString(2) );
            quote.setQuote( rs.getString(3) );
        }

        rs.close();
        
        return quote;
    }
}
