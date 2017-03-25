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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.spam.BotSpamDetector;

public class BotCommandInterpreter
{
    private String helpTrigger;
    private BotSpamDetector spamDetector;

    private HashMap<BotCommandEvent, List<BotCommand>> cmdListeners;

    public BotCommandInterpreter()
    {    
        this.spamDetector = new BotSpamDetector();
        this.helpTrigger =
                BotConfiguration.getConfig().getString("help_trigger", "!help");
        this.cmdListeners =
                new HashMap<>();
    }

    public void addListener(BotCommandEvent e, BotCommand command)
    {
        BotLogger.getDebugLogger().debug(command.getClass().getName());
        List<BotCommand> cmds;
        
        if (this.cmdListeners.containsKey(e)) {
            cmds = this.cmdListeners.get(e);
        } else {
            cmds = new LinkedList<BotCommand>();
        }
        
        cmds.add(command);
        this.cmdListeners.put(e, cmds);
    }

    private boolean isHelp(BotMessage msg)
    {
        return msg.getText().startsWith(this.helpTrigger);
    }
    
    private boolean isEmpty()
    {
        return this.cmdListeners.isEmpty();
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
            this.showHelp(msg);
        } else {
            this.processMessage(msg);
        }
    }

    private boolean isSpam(BotMessage msg)
    {
        BotUser user = msg.getUser();
        String target = msg.getTarget();
        if (this.spamDetector.checkForSpam(
                msg.getConn().hashCode() + target, user)) {
            BotLogger.getDebugLogger().debug("Spam Detected!");
            return true;
        }
        return false;
    }

    private List<BotCommand> findCommands(String cmdTrigger)
    {
        return this.cmdListeners.keySet().stream()
                .filter(e -> e.test(cmdTrigger))
                .flatMap(e -> this.cmdListeners.get(e).stream())
                .collect(Collectors.toList());
    }

    /**
     * Process message and fire command detected.
     * 
     * @param msg
     */
    private void processMessage(BotMessage msg)
    {
        List<BotCommand> cmds = this.findCommands(msg.getText().trim());
        if (cmds.isEmpty()) return;
        if (this.isSpam(msg)) return;

        this.execute(cmds, msg);
    }

    private void execute(List<BotCommand> cmds, BotMessage msg)
    {
        cmds.stream()
                .forEach(command -> new Thread(new BotCommandRunnable(command, msg)).start());
    }

    private void showHelp(BotMessage msg)
    {
        String cmdParams = msg.getParams();
        if (cmdParams.isEmpty()) {
            this.showEventTriggers(msg);
            return;
        }

        Pattern myPattern = Pattern.compile(cmdParams, Pattern.CASE_INSENSITIVE);
        this.cmdListeners.keySet().stream()
                .filter(event -> myPattern.matcher(event.getTrigger()).find())
                .forEach(e -> this.cmdListeners.get(e)
                                                .forEach(c -> msg.getConn().doPrivmsg(msg.getTarget(), c.help())));
    }

    private void showEventTriggers(BotMessage msg)
    {
        String triggers = this.cmdListeners.keySet().stream()
                .map(BotCommandEvent::getTrigger)
                .collect(Collectors.joining(", "));

        msg.getConn().doPrivmsg(msg.getTarget(), "Patterns: " + triggers);
    }
}
