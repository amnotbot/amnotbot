package org.knix.amnotbot.command;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictVeraParser implements DictParser
{

    public Vector<String> firstDefinition(DictDefinition definition)
    {
        Pattern firstDef;
        Matcher m;

        firstDef = Pattern.compile(definition.getWord(),
                Pattern.CASE_INSENSITIVE);
        m = firstDef.matcher(definition.getDefinition());

        String def = definition.getDefinition();
        Vector<String> output = new Vector<String>();
        if (m.find()) {
            output.add(def.substring(m.start(), def.length()));
        }
        return output;
    }
}
