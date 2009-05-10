package org.knix.amnotbot;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterConstructorTest
{

    public BotCommandInterpreterConstructorTest()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testConstruct()
    {
        System.out.println("construct");
        BotConnection conn = new BotConnection("localhost");
        BotCommandInterpreterConstructor c =
                new BotCommandInterpreterConstructor(
                    new BotCommandInterpreterBuilderFile()
                    );
        this.createCommandsFile();
        BotCommandInterpreter result = c.construct(conn);
        assertTrue(result != null);
    }

    private void createCommandsFile()
    {
        PropertiesConfiguration p;
        p = new PropertiesConfiguration();

        p.addProperty("GoogleCommand", "g");
        p.addProperty("QurlRequestCommand", "URL");
        p.addProperty("YahooNewsSearchCommand", "(y|yahoo)");

        try {
            p.save("commands.config");
        } catch (ConfigurationException e) {
            fail("Could not create commands.config file");
        }
    }
}