package org.knix.amnotbot;

import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterBuilderFile  
        extends BotCommandInterpreterBuilder
{
    
    private BotCommandInterpreter cmdInterpreter;

    public void buildInterpreter(BotSpamDetector spamDetector)
    {
        this.cmdInterpreter = new BotCommandInterpreter(spamDetector);
    }

    public BotCommandInterpreter getInterpreter()
    {
        return this.cmdInterpreter;
    }

    public void loadCommands()
    {
        Configuration cmdConfig;
        cmdConfig = BotConfiguration.getCommandsConfig();

        Iterator it = cmdConfig.getKeys();
        while (it.hasNext()) {
            String cname, fpath;
            cname = (String) it.next();
            fpath = "org.knix.amnotbot.command." + cname;
            Object o;
            try {
                o = Class.forName(fpath).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                BotLogger.getDebugLogger().debug(e);
                continue;
            }

            if (o instanceof BotCommand) {
                String trigger = cmdConfig.getString(cname);
                BotCommand cmd = (BotCommand)o;
                if (trigger.compareTo("URL") == 0) {
                    this.cmdInterpreter.addLinkListener(cmd);
                } else {
                    this.cmdInterpreter.addListener(
                            new BotCommandEvent(trigger), cmd
                            );
                }
            }
        }
    }
    
}