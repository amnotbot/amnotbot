package org.knix.amnotbot.command.utils;

public class CmdStringOption implements CmdOption
{

    private String name;
    private String arg0;
    private char delim;

    public CmdStringOption(String name, char delim)
    {
        this.delim = delim;
        this.arg0 = null;
        this.name = name;
    }

    public CmdStringOption(String name)
    {
        this.delim = '"';
        this.arg0 = null;
        this.name = name;
    }

    public void buildArgs(String msg)
    {
        this.arg0 = null;
        if (msg == null) return;
     
        int index = msg.indexOf(this.name + ":");
        if (index < 0) return;

        msg = msg.trim();
        index += this.name.length() + 1;
        index += msg.charAt(index) == this.delim ? 1 : 0;
        if (index > msg.length()) return;

        int rindex = msg.indexOf(':', index);
        for (int j = (rindex - 1); j > index; --j) {
            char c;
            c = msg.charAt(j);
            if (!Character.isLetterOrDigit(c)) break;
            rindex = j;
        }
        if (rindex < index) rindex = msg.length();

        String arg = new String();
        for (int i = index; i < rindex; ++i) {
            char c;
            c = msg.charAt(i);
            if (c == '\n' || c == '\0' || c == '\r' || c == this.delim) break;

            arg += c;
        }

        if (arg.length() > 0) {
            this.arg0 = arg.trim();
        }
    }

    public String getName()
    {
        return name;
    }

    private String stringValue()
    {
        if (this.hasValue()) {
            return this.arg0;
        }
        return "";
    }

    public boolean hasValue()
    {
        return (this.arg0 != null);
    }

    public String[] tokens()
    {
        String [] keywords;       
        keywords = new String[] { this.stringValue().trim() };
        return keywords;
    }
}
