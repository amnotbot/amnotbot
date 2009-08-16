package org.knix.amnotbot;

/**
 *
 * @author gpoppino
 */
public class BotMessage
{

    private String text;
    private BotUser user;
    private String target;    
    private BotConnection conn;

    public BotMessage(BotConnection conn,
            String target, BotUser user, String text)
    {
        this.conn = conn;
        this.user = user;
        this.target = target;
        this.text = text;
    }

    public BotUser getUser()
    {
        return this.user;
    }

    public String getTarget()
    {
        return this.target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public BotConnection getConn()
    {
        return this.conn;
    }
}
