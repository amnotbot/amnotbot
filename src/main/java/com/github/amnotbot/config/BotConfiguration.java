package com.github.amnotbot.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.github.amnotbot.BotLogger;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.lang.SystemUtils;

public final class BotConfiguration
{

    private static Configuration config = null;
    private static Configuration commands = null;
    private static Configuration tasks = null;
    private static Configuration pom = null;
    private static BotConfiguration botConfig = null;
    private static File home;
    

    protected BotConfiguration()
    {
        this.init();
    }
    
    private void init()
    {
        try {
            
            BotConfiguration.home = new File(SystemUtils.getUserHome(), 
                                            ".amnotbot");
            BotConfiguration.home.mkdirs();
            
            this.copyConfigFile("amnotbot.config");
            this.copyConfigFile("tasks.config");
            this.copyConfigFile("commands.config");
            this.copyConfigFile("log4j.properties");
            
        } catch (FileNotFoundException e) {
            BotLogger.getDebugLogger().debug(e);
        } catch (IOException e) {
            BotLogger.getDebugLogger().debug(e);
        }
    }
    
    private void copyConfigFile(String filename) 
            throws FileNotFoundException, IOException
    {
        File fileOnDisk = new File(BotConfiguration.home, 
                File.separator + filename);
        if (fileOnDisk.exists()) return;
        
        System.out.println("Copying file " + filename + " to " +
                fileOnDisk.getAbsolutePath());
        ClassLoader classLoader = this.getClass().getClassLoader();
        
        InputStream in = classLoader.getResourceAsStream(filename);
        OutputStream out = new FileOutputStream(fileOnDisk);

        int len;
        byte[] buf = new byte[1024];
        while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    private static String getConfigFilePath(String filename)
    {
        File f = new File(BotConfiguration.home, File.separator + filename);
        if (f.exists()) return f.getAbsolutePath();
        
        return filename;
    }

    public static Configuration getConfig()
    {
        if (botConfig == null) {
            botConfig = new BotConfiguration();
            
            String log4j = 
                    BotConfiguration.getConfigFilePath("log4j.properties");
            PropertyConfigurator.configure(log4j);
            try {
                String filename = 
                        BotConfiguration.getConfigFilePath("amnotbot.config");
                config = new PropertiesConfiguration(filename);
            } catch (ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
                if (config == null) {
                    config = new PropertiesConfiguration();
                }
            }
        }
        return config;
    }

    public static Configuration getCommandsConfig()
    {
        if (commands == null) {
            try {
                String filename = 
                        BotConfiguration.getConfigFilePath("commands.config");
                commands = new PropertiesConfiguration(filename);
            } catch (ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return commands;
    }
    
    public static Configuration getTasksConfig() 
    {
        if (tasks == null) {
            try {
                String filename = 
                        BotConfiguration.getConfigFilePath("tasks.config");
                tasks = new PropertiesConfiguration(filename);
            } catch (ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return tasks;
    }
    
    public static Configuration getPomProperties() 
    {
        if (pom == null) {
            try {
                pom = new PropertiesConfiguration("META-INF/maven/org.github.amnotbot/amnotbot-core/pom.properties");
            } catch (ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return pom;
    }
    
}
