package org.knix.amnotbot.command;

import java.util.Vector;

public interface DictParser
{

    public Vector<String> firstDefinition(DictDefinition definition);
}
