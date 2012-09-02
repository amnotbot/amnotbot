package com.github.amnotbot.task;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotTask;
import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.task.stats.LogFileScanner;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author gpoppino
 */
public class StatsTask extends BotTask
{
    private Properties p;
    
    public StatsTask()
    {        
        this.p = new Properties();
        this.p.setProperty("backend",
                BotConfiguration.getConfig().getString("backend"));
        this.p.setProperty("ignorefile",
                BotConfiguration.getConfig().getString("ignored_words_file"));
        this.p.setProperty("logdirectory",
                BotLogger.BOT_HOME + File.separator + "log");
        this.p.setProperty("botnick",
                BotConfiguration.getConfig().getString("nick"));
        this.p.setProperty("cmdtrigger",
                BotConfiguration.getConfig().getString("command_trigger", "."));
        this.p.setProperty("minwordlen",
                String.valueOf(BotConfiguration.getConfig().getInt("stats_min_word_length", 4)));
    }
    
    @Override
    public void run() 
    {
        LogFileScanner scanner;
        scanner = new LogFileScanner(p);
        
        scanner.scanLogFiles();
    }
}
