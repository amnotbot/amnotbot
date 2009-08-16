package org.knix.amnotbot;

import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.config.BotConfiguration;
import org.knix.amnotbot.spam.BotSpamDetector;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterBuilderFile  
        extends BotCommandInterpreterBuilder
{
    
    private BotCommandInterpreter cmdInterpreter;

    @Override
    public void buildInterpreter(BotSpamDetector spamDetector)
    {
        this.cmdInterpreter = new BotCommandInterpreter(spamDetector);
    }

    @Override
    public BotCommandInterpreter getInterpreter()
    {
        return this.cmdInterpreter;
    }

    @Override
    public void loadCommands()
    {
        Configuration cmdConfig;
        cmdConfig = BotConfiguration.getCommandsConfig();

        Iterator it = cmdConfig.getKeys();
        while (it.hasNext()) {
            String cname, fpath;
            cname = (String) it.next();
            fpath = "org.knix.amnotbot.cmd." + cname;
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

    @Override
    public BotSpamDetector buildSpamFilter(BotConnection conn)
    {
        return new BotSpamDetector();
    }
    
}
