package org.knix.amnotbot.cmd;

import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.BotCommand;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.config.BotConfiguration;

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
