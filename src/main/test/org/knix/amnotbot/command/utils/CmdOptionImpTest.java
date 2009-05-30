package org.knix.amnotbot.command.utils;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class CmdOptionImpTest
{

    public CmdOptionImpTest()
    {
    }

    @Test
    public void testBuildArgs()
    {
        CmdOptionImp cmd;
        cmd = new CmdOptionImp("text");

        cmd.buildArgs("text:message");      
        assertEquals(cmd.tokens()[0], "message");

        cmd.buildArgs(" op1:tom text:My message op2:123 op3:abc");
        assertEquals(cmd.tokens()[0], "My message");

        cmd.buildArgs("text:\"This is a long sentence.\"");        
        assertEquals(cmd.tokens()[0], "This is a long sentence.");

        cmd.buildArgs("This is a message");     
        assertEquals(cmd.tokens()[0], "");

        cmd.buildArgs("text:This is a short message.");        
        assertEquals(cmd.tokens()[0], "This is a short message.");
    }

    @Test
    public void testTokenizerBuildArgs()
    {
        CmdOptionImp colours = new CmdOptionImp("colours", ",");

        colours.buildArgs("colours:yellow,orange,black,green,white");
        assertTrue(Arrays.equals(colours.tokens(), 
                new String[] {"yellow", "orange", "black", "green", "white"})
                );

        colours.buildArgs("colours:violet,strong blue,soft pink");
        assertTrue(Arrays.equals(colours.tokens(), 
                new String[] {"violet", "strong blue", "soft pink"})
                );

        colours.buildArgs("colours: green,strong blue,soft pink opt2:abc");
        assertTrue(Arrays.equals(colours.tokens(),
                new String [] {"green", "strong blue", "soft pink"})
                );
    }

    @Test
    public void testHasValue()
    {
        CmdOptionImp cmd;
        cmd = new CmdOptionImp("text");

        cmd.buildArgs("text:message");
        assertTrue(cmd.hasValue());

        cmd.buildArgs(null);
        assertFalse(cmd.hasValue());

        cmd.buildArgs("op1:abc,def,ghi text:\"This is a message\" op2:123");
        assertTrue(cmd.hasValue());

        cmd.buildArgs("option:Testing option");
        assertFalse(cmd.hasValue());
    }
}