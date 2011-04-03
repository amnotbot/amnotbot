package java.org.knix.amnotbot;

import java.util.List;
import org.knix.amnotbot.BotConnection;

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
