package org.knix.amnotbot;

public interface CmdOption {	
	public void buildArgs(String msg);
	public String getName();
	public String stringValue(String sep);
	public String stringValue(String sep, String joinChar);
	public String stringValue();
	public boolean hasValue();
}
