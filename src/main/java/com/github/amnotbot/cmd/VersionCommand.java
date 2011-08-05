package com.github.amnotbot.cmd;

import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class VersionCommand implements BotCommand 
{

    @Override
    public void execute(BotMessage message) 
    {
        Configuration config = BotConfiguration.getPomProperties();
        String version = config.getString("artifactId") + " " +
                config.getString("version");
        
        message.getConn().doPrivmsg(message.getTarget(), version);
    }

    @Override
    public String help() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
