package org.knix.amnotbot.command;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Needs improvement. Sometimes fails.
 *       Should print one definition per type: noun, verb, adj.
 */
public class DictWordNetParser implements DictParser
{

    public DictWordNetParser()
    {
    }

    public Vector<String> firstDefinition(DictDefinition definition)
    {
        Pattern firstDef;
        Pattern secondDef;
        Matcher m, n;

        firstDef = Pattern.compile("((adj|[nv])+.[0-9]?:.*)",
                Pattern.CASE_INSENSITIVE);
        secondDef = Pattern.compile("(2:.*)", Pattern.CASE_INSENSITIVE);

        m = firstDef.matcher(definition.getDefinition());
        n = secondDef.matcher(definition.getDefinition());

        String def = definition.getDefinition();
        Vector<String> output = new Vector<String>();
        int s_index = 0, r_index = 0;
        while (m.find()) {
            if (n.find()) {
                output.add(def.substring(m.start(), n.start()));
            } else {
                s_index = m.start();
                if (m.find()) {
                    r_index = m.start();
                    output.add(def.substring(s_index, r_index));
                } else {
                    r_index = def.length();
                    output.add(def.substring(s_index, r_index));
                }
            }
        }
        return output;
    }
}
