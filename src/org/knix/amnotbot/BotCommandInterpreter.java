package org.knix.amnotbot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.schwering.irc.lib.IRCUser;


public class BotCommandInterpreter
{

    private Character cmdTrigger;
    private BotSpamDetector spamDetector;
    private LinkedList<BotCommand> commands;
    private LinkedList<BotCommand> linkListeners;
    private HashMap<BotCommandEvent, LinkedList<BotCommand>> cmdListeners;

    public BotCommandInterpreter(BotSpamDetector spamDetector)
    {    
        this.spamDetector = spamDetector;
        this.cmdTrigger = new Character('!');
        this.commands = new LinkedList<BotCommand>();     
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
            cmds.add(command);
        } else {
            cmds = new LinkedList<BotCommand>();
        }
        this.cmdListeners.put(e, cmds);
        this.commands.add(command);
    }

    public void addLinkListener(BotCommand command)
    {
        this.linkListeners.add(command);
    }

    public void remove(BotCommand command)
    {
        this.commands.remove(command);
    }

    public LinkedList<BotCommand> getCommands()
    {
        return this.commands;
    }

    private boolean isCommand(BotMessage msg)
    {
        if (msg.getText().charAt(0) != this.cmdTrigger) return false;
        return true;
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
        if (!this.isCommand(msg) &&
                !this.isLink(msg) ||
                this.isEmpty()) return;
       
        Iterator<BotCommandEvent> it = this.cmdListeners.keySet().iterator();
        while (it.hasNext()) {
            BotCommandEvent event = it.next();
            String trigger = this.getTrigger(msg);
            if (event.test(trigger)) {
                IRCUser user = msg.getUser();
                String target = msg.getTarget();
                if (this.spamDetector.checkForSpam(target, user)) {
                    BotLogger.getDebugLogger().debug("Spam Detected!");
                    return;
                }
                if (this.isCommand(msg))
                    this.removeTriggerString(msg);
                this.execute(event, msg);
            }
        }     
    }

    private void execute(BotCommandEvent event, BotMessage msg)
    {
        Iterator<BotCommand> cmds;
        cmds = this.cmdListeners.get(event).iterator();
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
