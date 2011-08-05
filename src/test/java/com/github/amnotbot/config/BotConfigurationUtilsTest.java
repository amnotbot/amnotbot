package com.github.amnotbot.config;

import java.util.ArrayList;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.amnotbot.config.BotConfigurationUtils;

import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class BotConfigurationUtilsTest {

    public BotConfigurationUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetRoots() throws ConfigurationException
    {
        System.out.println("getRoots");
        Configuration config = new PropertiesConfiguration("amnotbot.config");

        ArrayList<String> expResult = new ArrayList<String>();
        expResult.add("irc");                
        ArrayList<String> result = BotConfigurationUtils.getRoots(config);
        assertEquals(expResult, result);

        ArrayList<String> expResultSubset = new ArrayList<String>();
        expResultSubset.add("oftc");
        expResultSubset.add("freenode");
        ArrayList<String> resultSubset = BotConfigurationUtils.getRoots(
                config.subset("irc"));
        assertEquals(expResultSubset, resultSubset);
    }

}