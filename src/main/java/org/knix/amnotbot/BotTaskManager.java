package java.org.knix.amnotbot;

import java.util.LinkedList;
import java.util.Timer;

/**
 *
 * @author gpoppino
 */
public class BotTaskManager
{
    private Timer timer;
    private LinkedList<BotTask> tasks;

    public BotTaskManager()
    {
        this.tasks = new LinkedList<BotTask>();
        this.timer = new Timer();
    }

    public void addTask(BotTask task)
    {
        this.tasks.add(task);
        this.timer.scheduleAtFixedRate(task, 1000 * 60, task.getPeriod());
    }

    public void cancelTasks()
    {
        this.timer.cancel();
    }

}
