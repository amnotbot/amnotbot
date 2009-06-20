package org.knix.amnotbot.cmd.db;

import java.sql.SQLException;

public interface QuoteDAO
{
    public boolean save(QuoteEntity quote) throws SQLException;

    public boolean delete(int quoteId) throws SQLException;

    public QuoteEntity findById(int quoteId) throws SQLException;

    public QuoteEntity findRandom() throws SQLException;
}
