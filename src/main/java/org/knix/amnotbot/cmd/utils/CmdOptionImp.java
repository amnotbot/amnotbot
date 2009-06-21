package org.knix.amnotbot.cmd.utils;

public class CmdOptionImp implements CmdOption
{

    private char delim;
    private String name, arg0, tokenizer;

    public CmdOptionImp(String name, String tokenizer)
    {
        this.delim = '"';
        this.tokenizer = tokenizer;
        this.arg0 = null;
        this.name = name;
    }

    public CmdOptionImp(String name)
    {
        this.delim = '"';
        this.tokenizer = "\0";
        this.arg0 = null;
        this.name = name;
    }

    public void buildArgs(String msg)
    {
        this.arg0 = null;
        if (msg == null) return;

        msg = msg.trim();
        int index = msg.indexOf(this.name + ":");
        if (index < 0) return;
        
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

    public String [] tokens()
    {
        String[] values;

        if (this.hasValue()) {
            values = this.stringValue().split(this.tokenizer);
            for (int i = 0; i < values.length; ++i) {
                values[i] = values[i].trim();
            }
            return values;
        }
        return new String [] { null };
    }
}
