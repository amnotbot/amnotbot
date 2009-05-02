package org.knix.amnotbot.command.utils;

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

    private String colour;
    private String colour1;
    private String colour2;
    private String numbers;
    private String colour1Val1;
    private String colour1Val2;
    private String colour2Val1;
    private String colour2Val2;
    private String colour2Val3;
    private CmdCommaSeparatedOption colours;

    public CmdCommaSeparatedOptionTest()
    {
    }

    @Before
    public void setUp()
    {
        this.colours = new CmdCommaSeparatedOption("colours");

        this.colour1 = new String("colours:yellow,orange,black,green,white");
        this.colour1Val1 = new String("yellow,orange,black,green,white");
        this.colour1Val2 = new String("yellow orange black green white");
        
        this.colour2 = new String("colours:violet,strong blue,soft pink");
        this.colour2Val1 = new String("violet,strong blue,soft pink");
        this.colour2Val2 = new String("violet strong blue soft pink");
        this.colour2Val3 = new String("violet strong.blue soft.pink");

        this.numbers = new String("numbers:1,2,3,4,5");
        this.colour = new String("colour:yellow,green");
    }

    @Test
    public void hasValueTest()
    {
        this.colours.buildArgs(this.colour1);
        assertTrue(this.colours.hasValue());

        this.colours.buildArgs(this.colour2);
        assertTrue(this.colours.hasValue());

        this.colours.buildArgs(null);
        assertFalse(this.colours.hasValue());
    }

    @Test
    public void stringValueTest()
    {
        this.colours.buildArgs(this.colour1);
        assertEquals(this.colours.stringValue(), this.colour1Val1);

        this.colours.buildArgs(this.colour2);
        assertEquals(this.colours.stringValue(), this.colour2Val1);

        this.colours.buildArgs(null);
        assertTrue(this.colours.stringValue().isEmpty());

        this.colours.buildArgs(this.numbers);
        assertTrue(this.colours.stringValue().isEmpty());

        this.colours.buildArgs(this.colour);
        assertTrue(this.colours.stringValue().isEmpty());
    }

    @Test
    public void stringValueSepTest()
    {
        this.colours.buildArgs(this.colour1);        
        assertEquals(this.colours.stringValue(" "), this.colour1Val2);

        this.colours.buildArgs(this.colour2);
        assertEquals(this.colours.stringValue(" "), this.colour2Val2);

        this.colours.buildArgs(null);
        assertTrue(this.colours.stringValue(" ").isEmpty());

        this.colours.buildArgs(this.numbers);
        assertTrue(this.colours.stringValue(" ").isEmpty());

        this.colours.buildArgs(this.colour);
        assertTrue(this.colours.stringValue(" ").isEmpty());

        this.colours.buildArgs("colours:verde, negro.blanco,gris violeta a:12");
        String tmp1 = this.colours.stringValue("|");
        assertEquals(tmp1, "verde|negro.blanco|gris violeta");

        this.colours.buildArgs("op1:abc colours:verde,gris claro op:12 op3:de");
        String tmp2 = this.colours.stringValue(":");      
        assertEquals(tmp2, "verde:gris claro");
    }

    @Test
    public void stringValueCharjoinTest()
    {
        this.colours.buildArgs(this.colour1);
        assertEquals(this.colours.stringValue(" ", "."), this.colour1Val2);

        this.colours.buildArgs(this.colour2);
        assertEquals(this.colours.stringValue(" ", "."), this.colour2Val3);

        this.colours.buildArgs(null);
        assertTrue(this.colours.stringValue(" ", ".").isEmpty());

        this.colours.buildArgs(this.numbers);
        assertTrue(this.colours.stringValue(" ", ".").isEmpty());

        this.colours.buildArgs(this.colour);
        assertTrue(this.colours.stringValue(" ", ".").isEmpty());

        this.colours.buildArgs("colours:green, blue, yellow ,black");
        String tmp1 = this.colours.stringValue(" ", ".");
        assertEquals(tmp1, "green blue yellow black");
        
        this.colours.buildArgs("colours: green,strong blue,soft pink opt2:abc");
        String tmp2 = this.colours.stringValue(":", "-");
        assertEquals(tmp2, "green:strong-blue:soft-pink");

        this.colours.buildArgs("opt2:abc colours: green,strong blue,soft pink");
        String tmp3 = this.colours.stringValue("|", "_");
        assertEquals(tmp3, "green|strong_blue|soft_pink");
    }

    @After
    public void tearDown()
    {
    }
}