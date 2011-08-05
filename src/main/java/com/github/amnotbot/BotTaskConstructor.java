package com.github.amnotbot;

import java.util.List;

/**
 *
 * @author gpoppino
 */
public class BotTaskConstructor
{
    BotTaskBuilder builder;

    public BotTaskConstructor(BotTaskBuilder builder)
    {
        this.builder = builder;
    }


    public BotTaskManager construct(BotConnection conn, List<String> channels)
    {
        this.builder.buildTaskManager();
        this.builder.loadTasks(conn, channels);

        return this.builder.getTaskManager();
    }

}
