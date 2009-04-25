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
        if (msg == null) return;

        this.arg0 = null;

        int index = msg.indexOf(this.name + ":");
        if (index < 0) return;

        index += this.name.length() + 1;
        if (index > msg.length()) return;

        String arg = new String();
        for (int i = index; i < msg.length(); ++i) {
            char c;

            c = msg.charAt(i);
            if (c == ' ' || c == '\n' || c == '\0') break;

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

    public String stringValue(String sep, String joinChar)
    {
        String[] keywords;
        String value = "";

        if (this.arg0 != null) {
            String arg = this.arg0;
  
            keywords = arg.split(",");
            for (int j = 0; j < keywords.length; ++j) {
                String word = keywords[j];
                word = word.trim();
                word = word.replaceAll("\\s", joinChar);
                value += word + sep;
            }
        }

        return value.toLowerCase();
    }

    public String stringValue(String sep)
    {
        String[] keywords;
        String value = "";

        if (this.arg0 == null) return null;
       
        String arg = this.arg0;
        keywords = arg.split(",");
        for (int j = 0; j < keywords.length; ++j) {
            String word = keywords[j];
            word = word.trim();
            value += word + sep;
        }
    
        return value.toLowerCase();
    }

    public String stringValue()
    {
        if (this.hasValue()) {
            return this.arg0.toLowerCase();
        }
        return this.arg0;
    }

    public boolean hasValue()
    {
        return (this.arg0 != null);
    }
}
