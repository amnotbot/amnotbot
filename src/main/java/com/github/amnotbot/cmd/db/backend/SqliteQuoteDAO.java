package com.github.amnotbot.cmd.db.backend;


import com.github.amnotbot.cmd.db.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gpoppino
 */
public class SqliteQuoteDAO implements QuoteDAO
{
    private final String db;

    public SqliteQuoteDAO(String db)
    {
        this.db = db;
    }

    public boolean save(QuoteEntity quote) throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        int rowCount = -1;
        PreparedStatement smt;
        smt = c.prepareStatement(
                "INSERT INTO quotes (nick, desc) VALUES (?, ?)");
        smt.setString(1, quote.getUser());
        smt.setString(2, quote.getQuote());
        rowCount = smt.executeUpdate();
        smt.close();
        c.close();
        
        return (rowCount > 0);
    }

    public boolean delete(int quoteId) throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        int rowCount = -1;
        PreparedStatement smt;
        smt = c.prepareStatement("DELETE FROM quotes WHERE id = ?");
        smt.setInt(1, Integer.valueOf(quoteId));
        rowCount = smt.executeUpdate();
        smt.close();
        c.close();

        return (rowCount > 0);
    }

    public QuoteEntity findById(int quoteId) throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        ResultSet rs;
        PreparedStatement smt = c.prepareStatement(
                "SELECT * FROM quotes WHERE id = ?");
        smt.setInt(1, Integer.valueOf(quoteId));
        rs = smt.executeQuery();

        QuoteEntity quote = new QuoteEntity();
        quote.setId( rs.getInt(1) );
        quote.setUser( rs.getString(2) );
        quote.setQuote( rs.getString(3) );

        rs.close();
        smt.close();
        c.close();
        
        return quote;
    }

    public QuoteEntity findRandom() throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        ResultSet rs;        
        Statement smt = c.createStatement();
        rs = smt.executeQuery("SELECT * FROM quotes ORDER BY Random()");

        QuoteEntity quote = new QuoteEntity();
        quote.setId( rs.getInt(1) );
        quote.setUser( rs.getString(2) );
        quote.setQuote( rs.getString(3) );

        rs.close();
        smt.close();
        c.close();

        return quote;
    }

    public void createQuotesDB() throws SQLException
    {
        Connection c = BotDBFactory.instance().getConnection(this.db);

        Statement smt = c.createStatement();
        smt.executeUpdate("CREATE TABLE quotes " +
               "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
               " nick VARCHAR(50), desc VARCHAR(255))");
        smt.executeUpdate("CREATE UNIQUE INDEX _id ON quotes (id)");
        smt.executeUpdate("CREATE INDEX _nick ON quotes (nick)");

        smt.close();
        c.close();
    }

    public boolean quotesDBExists() throws SQLException
    {        
        Connection c = BotDBFactory.instance().getConnection(this.db);

        ResultSet rs = null;
        rs = c.getMetaData().getTables(null, null, null,
                new String[] {"TABLE"});

        boolean exists = false;
        if (rs.next()) {
            exists = true;
        }
        rs.close();
        c.close();

        return exists;
    }
}