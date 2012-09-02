package com.github.amnotbot.task.stats;

import java.io.File;
import java.util.Properties;

/**
 * Scans directories where irc log files are stored, and then feeds these files
 * to a StatsGenerator object. This object regenerates statistics based the
 * information stored on these log files.
 * 
 * @author gpoppino
 */
public class LogFileScanner
{
    private String logDirectory;
    private LogFileFilter filter;
    private StatsGenerator stats;

    /**
     * Scans subdirectories looking for IRC log files, and generates
     * a database for each log file with statistics about the channel.
     * @param p Properties contains the set of options set from the command
     * line.
     */
    public LogFileScanner(Properties p)
    {
        this.logDirectory = p.getProperty("logdirectory");
        this.filter = new LogFileFilter();
        this.stats = new StatsGenerator(p);
    }

    /**
     * Starts the scan of log files and generation of databases.
     */
    public void scanLogFiles()
    {        
        File dir = new File(this.logDirectory);
        String[] dlist = dir.list();
        for (String d : dlist) {
            File subdir = new File(this.logDirectory + "/" + d);
            String[] flist = subdir.list(this.filter);

            if (flist.length == 0) continue;

            for (String f : flist) {
                this.regenerateDB(subdir.getAbsolutePath() + "/" + f);
            }
        }
    }

    private void regenerateDB(String logFilename)
    {
        try {
            this.stats.regenerate(logFilename);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
