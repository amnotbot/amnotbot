package org.knix.amnotbot;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Needs improvement. Sometimes fails.
 * Should print one definition per type: noun, verb, adj.
 */
public class WordNetDictParser implements DictParser {

	public WordNetDictParser() {
	}

	public Vector<String> firstDefinition(Definition definition)
	{
		Pattern wnFirstDef = Pattern.compile("((adj|[nv])+.[0-9]?:.*)", Pattern.CASE_INSENSITIVE);
		Pattern wnSecondDef = Pattern.compile("(2:.*)", Pattern.CASE_INSENSITIVE);
		Matcher m = wnFirstDef.matcher(definition.getDefinition());
		Matcher n = wnSecondDef.matcher(definition.getDefinition());

		String myString = definition.getDefinition();
		Vector<String> myDefinition = new Vector<String>();
		int s_index = 0;
		int r_index = 0;
		while (m.find()) {
			if (n.find()) {
				myDefinition.add( myString.substring(m.start(), n.start()) );
			} else {
				s_index = m.start();
				if (m.find()) {
					r_index = m.start();
					myDefinition.add( myString.substring(s_index, r_index) );
				} else {
					r_index = myString.length();
					myDefinition.add( myString.substring(s_index, r_index) );
				}
			}
		}

		return myDefinition;
	}
}
