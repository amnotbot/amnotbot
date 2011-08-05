package com.github.amnotbot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.spam.BotSpamDetector;

public class BotCommandInterpreter
{
    private String cmdTrigger;
    private BotSpamDetector spamDetector;
    private LinkedList<BotCommand> linkListeners;
    private HashMap<BotCommandEvent, LinkedList<BotCommand>> cmdListeners;

    public BotCommandInterpreter(BotSpamDetector spamDetector)
    {    
        this.spamDetector = spamDetector;
        this.cmdTrigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
        this.cmdListeners =
                new HashMap<BotCommandEvent, LinkedList<BotCommand>>();
        this.linkListeners = new LinkedList<BotCommand>();
    }

    public void addListener(BotCommandEvent e, BotCommand command)
    {
        BotLogger.getDebugLogger().debug(command.getClass().getName());
        LinkedList<BotCommand> cmds;
        
        if (this.cmdListeners.containsKey(e)) {
            cmds = this.cmdListeners.get(e);
        } else {
            cmds = new LinkedList<BotCommand>();
        }
        
        cmds.add(command);
        this.cmdListeners.put(e, cmds);
    }

    public void addLinkListener(BotCommand command)
    {
        this.linkListeners.add(command);
    }

    private boolean isHelp(BotMessage msg)
    {
        String text = msg.getText();

        if (text.startsWith(this.cmdTrigger + this.cmdTrigger))
            return true;
        else
            return false;
    }

    private boolean isCommand(BotMessage msg)
    {
        String text = msg.getText();
        
        if (StringUtils.isNotBlank(text) && text.startsWith(cmdTrigger))
            return true;
        else
            return false;
    }
    
    private boolean isLink(BotMessage msg)
    {
        Pattern p = Pattern.compile(
                "^((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)");
        Matcher m = p.matcher(msg.getText());

        return m.find() ? true : false;
    }
    
    private boolean isEmpty()
    {
        return (this.cmdListeners.isEmpty() && this.linkListeners.isEmpty());
    }

    public void run(BotMessage msg)
    {
        if (this.isEmpty()) return;

        if (this.isHelp(msg)) {
            this.processHelpRequest(msg);
        } else if (this.isCommand(msg)) {
            this.processMessage(msg, false);
        } else if (this.isLink(msg)) {
            this.execute(this.linkListeners, msg);
        }
    }

    private String getTrigger(String text)
    {
        String trigger = new String();

        for (int i = 1; i < text.length(); ++i) {
            char c = text.charAt(i);

            if (!Character.isLetterOrDigit(c)) break;
            trigger += c;
        }

        return trigger;
    }

    private void processHelpRequest(BotMessage msg)
    {
        String trigger = this.getTrigger(msg.getText().substring(1));
        if (trigger.isEmpty()) {
            this.showEventTriggers(msg);
        } else if (trigger.equals("url")) {
            this.showHelp(this.linkListeners, msg);
        } else {
            msg.setText( msg.getText().substring(1) );
            this.processMessage(msg, true);
        }
    }

    private void processMessage(BotMessage msg, boolean help)
    {
        String trigger = this.getTrigger(msg.getText());
        Iterator<BotCommandEvent> it = this.cmdListeners.keySet().iterator();
        
        while (it.hasNext()) {
            BotCommandEvent event = it.next();
            
            if (event.test(trigger)) {
                BotUser user = msg.getUser();
                String target = msg.getTarget();
                
                if (this.spamDetector.checkForSpam(target, user)) {
                    BotLogger.getDebugLogger().debug("Spam Detected!");
                    return;
                }

                msg.setText( this.removeTriggerString(msg.getText()) );
                if (help) {
                    this.showHelp(this.cmdListeners.get(event), msg);
                } else {
                    this.execute(this.cmdListeners.get(event), msg);
                }
            }
        }
    }
    
    private void execute(LinkedList<BotCommand> l, BotMessage msg)
    {
        Iterator<BotCommand> cmds;
        cmds = l.iterator();
        
        while (cmds.hasNext()) {
            BotCommand command = cmds.next();
            new Thread(new BotCommandRunnable(command, msg)).start();
        }
    }

    private void showHelp(LinkedList<BotCommand> l, BotMessage msg)
    {
        Iterator<BotCommand> cmds;
        cmds = l.iterator();

        while (cmds.hasNext()) {
            BotCommand command = cmds.next();
            msg.getConn().doPrivmsg(msg.getTarget(), command.help());
        }
    }

    private String removeTriggerString(String text)
    {
        return text.substring(this.getTrigger(text).length() + 1,
                text.length());
    }

    private void showEventTriggers(BotMessage msg)
    {
        Iterator<BotCommandEvent> it = this.cmdListeners.keySet().iterator();

        String triggersList = new String();
        while (it.hasNext()) {
            BotCommandEvent event = it.next();
            triggersList += event.getTrigger() + " ";
        }
        triggersList += "url";
        msg.getConn().doPrivmsg(msg.getTarget(), triggersList);
    }
}
