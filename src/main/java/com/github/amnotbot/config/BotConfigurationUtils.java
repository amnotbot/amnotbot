package com.github.amnotbot.config;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author gpoppino
 */
public class BotConfigurationUtils
{
    public static ArrayList<String> getRoots(Configuration config)
    {
        Iterator<String> it = config.getKeys();

        ArrayList<String> roots = new ArrayList();
        while (it.hasNext()) {
            String key = it.next();
            if (!key.contains(".")) continue;

            String root = StringUtils.substringBefore(key, ".");
            if (!roots.contains(root)) {
                roots.add(root);
            }
        }
        return roots;
    }
}
