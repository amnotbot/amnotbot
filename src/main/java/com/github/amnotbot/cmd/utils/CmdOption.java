package com.github.amnotbot.cmd.utils;

public interface CmdOption
{

    public void buildArgs(String msg);
    public String getName();
    public String [] tokens();
    public boolean hasValue();

}
