package com.github.amnotbot.task.stats;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author gpoppino
 */
public class DbFileFilter implements FilenameFilter
{

    @Override
    public boolean accept(File dir, String name)
    {
        return (name.startsWith("#") && name.contains(".db"));
    }

}
