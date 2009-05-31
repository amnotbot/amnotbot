package org.knix.amnotbot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.knix.amnotbot.config.BotConfiguration;
import org.schwering.irc.lib.IRCUser;


public class BotCommandInterpreter
{

    private String cmdTrigger;
    private BotSpamDetector spamDetector;
    private LinkedList<BotCommand> linkListeners;
    private HashMap<BotCommandEvent, LinkedList<BotCommand>> cmdListeners;

    public BotCommandInterpreter(BotSpamDetector spamDetector)
    {    
        this.spamDetector = spamDetector;
        this.cmdTrigger = BotConfiguration.getConfig().getString("command_trigger");     
        this.cmdListeners = new 
                HashMap<BotCommandEvent, LinkedList<BotCommand>>();
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
        String url;
        url = "^((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)";
        Pattern p = Pattern.compile(url);
        Matcher m = p.matcher(msg.getText());

        if (!m.find()) return false;
        return true;
    }

    private String getTrigger(BotMessage msg)
    {
        String trigger, text;
                
        text = msg.getText();
        trigger = new String();
        for (int i = 1; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (!Character.isLetterOrDigit(c)) break;
            trigger += c;
        }
        return trigger;
    }

    private boolean isEmpty()
    {
        return (this.cmdListeners.isEmpty() && this.linkListeners.isEmpty());
    }

    public void run(BotMessage msg)
    {
        if (this.isEmpty()) return;
        
        if (this.isCommand(msg)) {
            this.processMessage(msg);
        } else if (this.isLink(msg)) {
            this.execute(this.linkListeners, msg);
        }
    }

    private void processMessage(BotMessage msg)
    {
        String trigger = this.getTrigger(msg);
        Iterator<BotCommandEvent> it = this.cmdListeners.keySet().iterator();
        while (it.hasNext()) {
            BotCommandEvent event = it.next();            
            if (event.test(trigger)) {               
                IRCUser user = msg.getUser();
                String target = msg.getTarget();
                if (this.spamDetector.checkForSpam(target, user)) {          
                    BotLogger.getDebugLogger().debug("Spam Detected!");
                    return;
                }
                this.removeTriggerString(msg);
                this.execute(this.cmdListeners.get(event), msg);
            }
        }     
    }

    private void execute(LinkedList<BotCommand> l, BotMessage msg)
    {
        Iterator<BotCommand> cmds;
        cmds = l.iterator();
        while (cmds.hasNext()) {
            BotCommand command = cmds.next();
            command.execute(msg);
        }
    }

    private void removeTriggerString(BotMessage msg)
    {
        String text = msg.getText();
        text = text.substring(this.getTrigger(msg).length() + 1, text.length());
        msg.setText(text);
    }    
}
