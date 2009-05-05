package org.knix.amnotbot.command.utils;

import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class CommandOptionsTest
{

    String tags_option;
    String [] tags_option_value;
    String text_option, text_option_value;
    CommandOptions cmdOptions;

    public CommandOptionsTest()
    {
        this.cmdOptions =
                new CommandOptions("tree text:message tags:abc,def,ghi");
        this.tags_option = new String("tags");
        this.text_option = new String("text");
        this.text_option_value = new String("message");
        this.tags_option_value = new String [] {"abc", "def", "ghi"};
    }

    @Before
    public void setUp()
    {                
        this.cmdOptions.addOption(
                new CmdCommaSeparatedOption(this.tags_option)
                );
        this.cmdOptions.addOption(new CmdStringOption(this.text_option));
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testBuildArgs()
    {
        this.cmdOptions.buildArgs();

        CmdOption textOpt;
        textOpt = this.cmdOptions.getOption(this.text_option);
        assertEquals(textOpt.tokens()[0], this.text_option_value);

        CmdOption commaOpt;
        commaOpt = this.cmdOptions.getOption(this.tags_option);
        assertTrue(Arrays.equals(commaOpt.tokens(), this.tags_option_value));
    }

    @Test
    public void testGetOption()
    {
        CmdOption textOpt;
        textOpt = this.cmdOptions.getOption(this.text_option);
        assertEquals(textOpt.getName(), this.text_option);

        CmdOption commaOpt;
        commaOpt = (CmdCommaSeparatedOption) 
                this.cmdOptions.getOption(this.tags_option);
        assertEquals(commaOpt.getName(), this.tags_option);

        assertNull(this.cmdOptions.getOption("comment"));
    }

    @Test
    public void testHasOptions()
    {
        this.cmdOptions.buildArgs();
        assertTrue(this.cmdOptions.hasOptions());
    }

    @Test
    public void testOptionStartAt()
    {
        assertEquals(this.cmdOptions.optionStartAt(), 5);
    }
}