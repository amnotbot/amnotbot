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
        System.err.println(cmd.stringValue());
        assertEquals(cmd.stringValue(), "message");

        cmd.buildArgs("op1:tom text:My message op2:123 op3:abc");
        System.err.println(cmd.stringValue());
        assertEquals(cmd.stringValue(), "My message");

        cmd.buildArgs("text:\"This is a long sentence.\"");
        System.err.println(cmd.stringValue());
        assertEquals(cmd.stringValue(), "This is a long sentence.");

        cmd.buildArgs("This is a message");
        System.err.println(cmd.stringValue());
        assertEquals(cmd.stringValue(), "");

        cmd.buildArgs("text:This is a short message.");
        System.err.println(cmd.stringValue());
        assertEquals(cmd.stringValue(), "This is a short message.");

        CmdStringOption cmd1;
        cmd1 = new CmdStringOption("m", '\'');
        cmd1.buildArgs("m:'Brief message'");
        assertEquals(cmd1.stringValue(), "Brief message");
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
    }
}