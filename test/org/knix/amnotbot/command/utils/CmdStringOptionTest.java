package org.knix.amnotbot.command.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class CmdStringOptionTest
{

    public CmdStringOptionTest()
    {
    }

    @Test
    public void testBuildArgs()
    {
        CmdStringOption cmd;
        cmd = new CmdStringOption("text");

        cmd.buildArgs("text:message");      
        assertEquals(cmd.tokens()[0], "message");

        cmd.buildArgs("op1:tom text:My message op2:123 op3:abc");      
        assertEquals(cmd.tokens()[0], "My message");

        cmd.buildArgs("text:\"This is a long sentence.\"");        
        assertEquals(cmd.tokens()[0], "This is a long sentence.");

        cmd.buildArgs("This is a message");     
        assertEquals(cmd.tokens()[0], "");

        cmd.buildArgs("text:This is a short message.");        
        assertEquals(cmd.tokens()[0], "This is a short message.");

        CmdStringOption cmd1;
        cmd1 = new CmdStringOption("m", '\'');
        cmd1.buildArgs("m:'Brief message'");
        assertEquals(cmd1.tokens()[0], "Brief message");
    }

    @Test
    public void testHasValue()
    {
        CmdStringOption cmd;
        cmd = new CmdStringOption("text");

        cmd.buildArgs("text:message");
        assertTrue(cmd.hasValue());

        cmd.buildArgs(null);
        assertFalse(cmd.hasValue());

        cmd.buildArgs("text:This is a message");
        assertTrue(cmd.hasValue());

        cmd.buildArgs("option:Testing option");
        assertFalse(cmd.hasValue());
    }
}