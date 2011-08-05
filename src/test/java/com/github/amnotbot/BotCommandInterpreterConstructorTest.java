package com.github.amnotbot;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.amnotbot.BotCommandInterpreter;
import com.github.amnotbot.BotCommandInterpreterBuilderFile;
import com.github.amnotbot.BotCommandInterpreterConstructor;
import com.github.amnotbot.BotConnection;

import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterConstructorTest
{
    private final String configFile = "commands.config";

    public BotCommandInterpreterConstructorTest()
    {
    }

    @Before
    public void setUp()
    {
        this.createCommandsFile();
    }

    @After
    public void tearDown()
    {
        this.deleteCommandsFile();
    }

    @Test
    public void testConstruct()
    {
        System.out.println("construct");
        BotConnection conn = new DummyConnection();
        BotCommandInterpreterConstructor c =
                new BotCommandInterpreterConstructor(
                    new BotCommandInterpreterBuilderFile()
                    );        
        BotCommandInterpreter result = c.construct(conn);
        assertTrue(result != null);
    }

    private void createCommandsFile()
    {
        PropertiesConfiguration p;
        p = new PropertiesConfiguration();

        p.addProperty("GoogleWebSearchCommand", "g");
        p.addProperty("QurlRequestCommand", "URL");
        p.addProperty("YahooNewsSearchCommand", "(y|yahoo)");

        try {
            p.save(this.configFile);
        } catch (ConfigurationException e) {
            fail("Could not create commands.config file");
        }
    }

    private void deleteCommandsFile()
    {
        File file = new File(this.configFile);
        file.delete();
    }
}