package org.knix.amnotbot.command;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: gresco
 * Date: Oct 21, 2007
 * Time: 11:01:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DictParser {

	public Vector<String> firstDefinition(Definition definition);

}
