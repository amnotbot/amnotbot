package com.github.amnotbot.task.stats;

import java.util.Date;

/**
 *
 * @author gpoppino
 */
class StatsRecordEntity implements StatsRecordDAO
{
    private Date date;
    private String nick;
    private String word;

    public StatsRecordEntity(Date date, String nick, String word)
    {
        this.date = date;
        this.nick = nick;
        this.word = word;
    }

    @Override
    public Date getDate()
    {
        return this.date;
    }

    @Override
    public String getNick()
    {
        return this.nick;
    }

    @Override
    public String getWord()
    {
        return this.word;
    }

}
