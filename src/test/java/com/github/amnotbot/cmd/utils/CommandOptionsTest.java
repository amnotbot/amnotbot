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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.amnotbot.cmd.utils.CmdOption;
import com.github.amnotbot.cmd.utils.CmdOptionImp;
import com.github.amnotbot.cmd.utils.CommandOptions;

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
                new CmdOptionImp(this.tags_option, ",")
                );
        this.cmdOptions.addOption(new CmdOptionImp(this.text_option));
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
        commaOpt = this.cmdOptions.getOption(this.tags_option);
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