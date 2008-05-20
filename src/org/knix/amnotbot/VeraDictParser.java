package org.knix.amnotbot;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: gresco
 * Date: Oct 21, 2007
 * Time: 11:09:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class VeraDictParser implements DictParser {

	public Vector<String> firstDefinition(Definition definition) 
	{
		Vector<String> myDefinition = new Vector<String>();
		String myString = definition.getDefinition();

		Pattern veraFirstDef = Pattern.compile(definition.getWord(), Pattern.CASE_INSENSITIVE);
		Matcher m = veraFirstDef.matcher(definition.getDefinition());

		if (m.find()) {
			System.out.println("vera def" + myString.substring(m.start(), myString.length()));
			myDefinition.add( myString.substring(m.start(), myString.length()) );
		}

		return myDefinition;
	}
}
