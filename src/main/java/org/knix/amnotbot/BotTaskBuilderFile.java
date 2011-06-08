package org.knix.amnotbot;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotTaskBuilderFile extends BotTaskBuilder
{

    private BotTaskManager taskManager;

    @Override
    public void buildTaskManager()
    {
        this.taskManager = new BotTaskManager();
    }

    @Override
    public BotTaskManager getTaskManager() 
    {
        return this.taskManager;
    }

    @Override
    public void loadTasks(BotConnection conn, List<String> channels)
    {
        Configuration cmdConfig;
        cmdConfig = BotConfiguration.getTasksConfig();

        Iterator it = cmdConfig.getKeys();
        while (it.hasNext()) {
            String cname, fpath;
            cname = (String) it.next();
            fpath = "org.knix.amnotbot.task." + cname;
            Object o;
            try {
                o = Class.forName(fpath).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                BotLogger.getDebugLogger().debug(e);
                continue;
            }

            if (o instanceof BotTask) {
                int period = cmdConfig.getInt(cname);
                BotTask cmd = (BotTask)o;
                cmd.setPeriod(period);
                cmd.setConnection(conn);
                cmd.setChannels(channels);
                this.taskManager.addTask(cmd);
            }
        }
    }

}
