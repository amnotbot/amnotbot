package org.knix.amnotbot;

import org.apache.commons.configuration.*;

public class BotConfiguration {

	private static BotConfiguration botConfig = null;
	private static Configuration config = null;
	
	protected BotConfiguration() {
		
	}
	
	public static Configuration getConfig() 
	{
		if (botConfig == null) {
			botConfig = new BotConfiguration();
			
			try {
				config = new PropertiesConfiguration("amnotbot.config");
			} catch (ConfigurationException e) {
				e.printStackTrace();
				System.err.println("Problem loading amnotbot.config file");
			}
		}
		
		return config;
	}
	
}
