package com.github.amnotbot.cmd.db;

import java.sql.SQLException;

public interface QuoteDAO
{
    
    public boolean save(QuoteEntity quote) throws SQLException;

    public boolean delete(int quoteId) throws SQLException;

    public QuoteEntity findById(int quoteId) throws SQLException;

    public QuoteEntity findRandom() throws SQLException;

    public void createQuotesDB() throws SQLException;

    public boolean quotesDBExists() throws SQLException;

}
