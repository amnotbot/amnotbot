package java.org.knix.amnotbot;

import java.util.List;
import org.knix.amnotbot.BotConnection;

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
