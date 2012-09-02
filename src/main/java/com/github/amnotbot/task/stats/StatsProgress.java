package com.github.amnotbot.task.stats;

/**
 * Shows the percentage progress of a task.
 * @author gpoppino
 */
public class StatsProgress 
{
    
    private long total;
    private String filename;
    private String[] wheel;
    /**
     * @param filename Name of the file being read.
     * @param total Number that represents the total to complete.
     */
    StatsProgress(String filename, long total)
    {
        this.total = total;
        this.wheel = new String[] { "-", "\\", "|", "/" };
        this.filename = filename;
    }
    
    /**
     * Shows the progress of the task.
     * @param progress How far the task has progressed.
     */
    public void showProgress(long progress)
    {
        long completed = progress * 100 / this.total;
        
        int p = (int) (completed % this.wheel.length);
        System.out.print("[" + this.filename + "] " + this.wheel[p] + " %" 
                + completed + "\r");
    }
    
    /**
     * Sets the progress to %100.
     */
    public void finish()
    {
        System.out.print("[" + this.filename + "] " + this.wheel[0] + " %100\r");
        System.out.println();
    }
}
