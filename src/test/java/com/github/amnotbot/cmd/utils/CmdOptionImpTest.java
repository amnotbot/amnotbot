/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.amnotbot.cmd.utils;

import java.util.Arrays;
import org.junit.Test;

import com.github.amnotbot.cmd.utils.CmdOptionImp;

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
        assertEquals(cmd.tokens()[0], null);

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