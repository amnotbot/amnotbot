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
        this.delim = ' ';
        this.arg0 = null;
        this.name = name;
    }

    public void buildArgs(String msg)
    {
        int index;
        String arg = new String();

        this.arg0 = null;

        if (msg == null) return;

        int dlen = this.delim == ' ' ? 0 : 1;
        index = msg.indexOf(this.name + ":");
        if (index >= 0) {
            index += this.name.length() + 1 + dlen; // skip ':'
            if (index < msg.length()) {
                for (int i = index; i < msg.length(); ++i) {
                    char c;

                    c = msg.charAt(i);
                    if (c == this.delim) break;
 
                    arg += c;
                }

                if (arg.length() > 0) {
                    this.arg0 = arg.trim();
                }
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public String stringValue(String sep)
    {
        return this.stringValue();
    }

    public String stringValue(String sep, String joinChar)
    {
        return this.stringValue();
    }

    public String stringValue()
    {
        return this.arg0;
    }

    public boolean hasValue()
    {
        return (this.arg0 != null);
    }
}
