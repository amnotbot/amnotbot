/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
    
    /** Collection of commands to fire for links. */
    private LinkedList<BotCommand> linkListeners;
    
    /** Map of commands */
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
                ".*((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)");
        Matcher m = p.matcher(msg.getText());

        return m.find() ? true : false;
    }
    
    private boolean isEmpty()
    {
        return (this.cmdListeners.isEmpty() && this.linkListeners.isEmpty());
    }

    /**
     * Process a new message.
     * 
     * @param msg
     */
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

    /** Get the trigger in a string */
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

    /** Process a help request */
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

    /**
     * Process message and fire command detected.
     * 
     * @param msg
     * @param help
     */
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

                // Strip the !command prefix from the message
                msg.setText( this.removeTriggerString(msg.getText()) );
                if (help) {
                    this.showHelp(this.cmdListeners.get(event), msg);
                } else {
                    this.execute(this.cmdListeners.get(event), msg);
                }
            }
        }
    }
    
    /**
     * Executes all commands in List l with BotMessage
     * 
     * @param l List of BotCommands to execute
     * @param msg Message sent to commands
     */
    private void execute(LinkedList<BotCommand> l, BotMessage msg)
    {
        Iterator<BotCommand> cmds;
        cmds = l.iterator();
        
        while (cmds.hasNext()) {
            BotCommand command = cmds.next();
            new Thread(new BotCommandRunnable(command, msg)).start();
        }
    }

    /**
     * Show help for the given collection of commands
     * 
     * @param l
     * @param msg
     */
    private void showHelp(LinkedList<BotCommand> l, BotMessage msg)
    {
        Iterator<BotCommand> cmds;
        cmds = l.iterator();

        while (cmds.hasNext()) {
            BotCommand command = cmds.next();
            msg.getConn().doPrivmsg(msg.getTarget(), command.help());
        }
    }

    /** Strip the !command prefix from a string */
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
