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
public class CmdCommaSeparatedOptionTest
{

    private String colour1, colour2;
    private String numbers, color;
    private String [] colourTokens1;
    private String [] colourTokens2;

    private CmdCommaSeparatedOption colours;

    public CmdCommaSeparatedOptionTest()
    {
    }

    @Before
    public void setUp()
    {
        this.colours = new CmdCommaSeparatedOption("colours");

        this.colour1 = new String("colours:yellow,orange,black,green,white");
        this.colourTokens1 = new String[] {"yellow", "orange", "black",
                                                            "green", "white"};
        
        this.colour2 = new String("colours:violet,strong blue,soft pink");
        this.colourTokens2 = new String[] {"violet", "strong blue",
                                                                "soft pink"};

        this.numbers = new String("numbers:1,2,3,4,5");
        this.color = new String("colour:yellow,green");
    }

    @Test
    public void hasValueTest()
    {
        this.colours.buildArgs(this.colour1);
        assertTrue(this.colours.hasValue());

        this.colours.buildArgs(this.colour2);
        assertTrue(this.colours.hasValue());

        this.colours.buildArgs(this.color);
        assertFalse(this.colours.hasValue());

        this.colours.buildArgs(this.numbers);
        assertFalse(this.colours.hasValue());
    }
    
    @Test
    public void testTokens()
    {
        this.colours.buildArgs(this.colour1);
        assertTrue(Arrays.equals(this.colours.tokens(), this.colourTokens1));

        this.colours.buildArgs(this.colour2);
        assertTrue(Arrays.equals(this.colours.tokens(), this.colourTokens2));

        this.colours.buildArgs("colours: green,strong blue,soft pink opt2:abc");
        assertTrue(Arrays.equals(this.colours.tokens(),
                new String [] {"green", "strong blue", "soft pink"})
                );
    }

    @After
    public void tearDown()
    {
    }
}