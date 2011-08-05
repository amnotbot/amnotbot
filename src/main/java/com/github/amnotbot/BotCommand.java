package com.github.amnotbot;

public interface BotCommand
{
 
    public void execute(BotMessage message);
    public String help();
    
}
