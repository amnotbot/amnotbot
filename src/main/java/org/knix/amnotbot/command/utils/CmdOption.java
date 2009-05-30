package org.knix.amnotbot.command.utils;

public interface CmdOption
{

    public void buildArgs(String msg);
    public String getName();
    public String [] tokens();
    public boolean hasValue();

}
