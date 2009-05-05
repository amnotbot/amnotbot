package org.knix.amnotbot.command.utils;

public class CmdCommaSeparatedOption implements CmdOption
{

    private String name;
    private String arg0;

    public CmdCommaSeparatedOption(String name)
    {
        this.name = name;
        this.arg0 = null;
    }

    public void buildArgs(String msg)
    {
        this.arg0 = null;        
        if (msg == null) return;
       
        int index = msg.indexOf(this.name + ":");
        if (index < 0) return;

        msg = msg.trim();
        index += this.name.length() + 1;
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
            if (c == '\n' || c == '\0' || c == '\r') break;

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

    public String [] tokens()
    {
        String[] values;

        if (this.hasValue()) {
            values = this.stringValue().split(",");
            for (int i = 0; i < values.length; ++i) {
                values[i] = values[i].trim();
            }
            return values;
        }
        return null;
    }

    private String stringValue()
    {
        if (this.hasValue()) {
            return this.arg0.toLowerCase();
        }
        return "";
    }

    public boolean hasValue()
    {
        return (this.arg0 != null);
    }
}