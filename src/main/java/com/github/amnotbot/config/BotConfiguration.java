package com.github.amnotbot.config;

import java.io.File;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

import com.github.amnotbot.BotLogger;

public class BotConfiguration
{

    private static Configuration config = null;
    private static Configuration commands = null;
    private static Configuration tasks = null;
    private static Configuration pom = null;
    private static BotConfiguration botConfig = null;

    protected BotConfiguration()
    {
    }

    public static Configuration getConfig()
    {
        if (botConfig == null) {
            botConfig = new BotConfiguration();
            
            String log4j = "log4j.properties";
            boolean exists = (new File(log4j)).exists();
            if (exists) {
                PropertyConfigurator.configure(log4j);
            }
            try {                
                config = new PropertiesConfiguration("amnotbot.config");
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
                commands = new PropertiesConfiguration("commands.config");
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
                tasks = new PropertiesConfiguration("tasks.config");
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
                pom = new PropertiesConfiguration("META-INF/maven/org.bitbucket.amnotbot/amnotbot-core/pom.properties");
            } catch (ConfigurationException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
        return pom;
    }
    
}
