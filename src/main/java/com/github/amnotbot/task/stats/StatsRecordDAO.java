package com.github.amnotbot.task.stats;

import java.util.Date;

/**
 * Represents a statistics table record.
 * @author gpoppino
 */
public interface StatsRecordDAO
{

    /**
     * Returns the date of this record.
     * @return The original date of this record (recored in the log file).
     */
    public Date getDate();
    /**
     * Returns the nick of this record.
     * @return A string with the nick name of the chat user of this record
     * (recorded in the log file).
     */
    public String getNick();
    /**
     * Returns the word associated with this record.
     * @return A string with a word associated with the chat user
     * of this record (recorded in a chat line of the log file).
     */
    public String getWord();

}
