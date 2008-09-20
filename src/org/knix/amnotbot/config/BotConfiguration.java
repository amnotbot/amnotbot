package org.knix.amnotbot.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;
import org.knix.amnotbot.BotLogger;

public class BotConfiguration {

	private static BotConfiguration botConfig = null;
	private static Configuration config = null;
	
	protected BotConfiguration() {
		
	}
	
	public static Configuration getConfig() 
	{
		if (botConfig == null) {
			botConfig = new BotConfiguration();
            
            PropertyConfigurator.configure("log4j.properties");
			
			try {
				config = new PropertiesConfiguration("amnotbot.config");
			} catch (ConfigurationException e) {
				e.printStackTrace();
				BotLogger.getDebugLogger().debug("Problem loading amnotbot.config file");
			}
		}
		
		return config;
	}
	
}
