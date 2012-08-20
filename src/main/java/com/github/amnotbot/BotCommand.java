package com.github.amnotbot;

/**
 * A BotCommand is executed when its configured trigger is detected.
 */
public interface BotCommand
{
	/** Execute the command */
    public void execute(BotMessage message);
    
    /** Display help information for this command */
    public String help();
    
}
