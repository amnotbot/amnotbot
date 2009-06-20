package org.knix.amnotbot.cmd.db;

/**
 *
 * @author gpoppino
 */
public class QuoteEntity
{
    int id;
    String user;
    String quote;

    public QuoteEntity()
    {
        this.id = -1;
        this.user = null;
        this.quote = null;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public void setQuote(String quote)
    {
        this.quote = quote;
    }

    public int getId()
    {
        return this.id;
    }

    public String getUser()
    {
        return this.user;
    }

    public String getQuote()
    {
        return this.quote;
    }    
}
