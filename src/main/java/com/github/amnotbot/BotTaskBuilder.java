package com.github.amnotbot;

import java.util.List;

/**
 *
 * @author gpoppino
 */
public abstract class BotTaskBuilder
{
    public abstract void buildTaskManager();

    public abstract BotTaskManager getTaskManager();

    public abstract void loadTasks(BotConnection conn,
            List<String> chans);
}
