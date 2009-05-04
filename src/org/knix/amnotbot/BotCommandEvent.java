package org.knix.amnotbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gpoppino
 */
public class BotCommandEvent
{

    private Pattern trigger;

    public BotCommandEvent(String trigger)
    {
        this.trigger = Pattern.compile(trigger, Pattern.CASE_INSENSITIVE);
    }

    public boolean test(String msg)
    {
        Matcher m;
        m = this.trigger.matcher(msg);
        if (m.find()) return true;
        return false;
    }
}
