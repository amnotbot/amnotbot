package org.knix.amnotbot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knix.amnotbot.config.BotConfiguration;
import org.knix.amnotbot.spam.BotSpamDetector;
import org.schwering.irc.lib.IRCUser;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterTest
{

    private SharedObject s;
    private BotConnection conn;

    public BotCommandInterpreterTest()
    {
    }

    @Before
    public void setUp()
    {
        this.s = new SharedObject();
        this.conn = new BotConnection("localhost");
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testAddListener()
    {
        System.out.println("addListener");
        IRCUser user = new IRCUser("gresco1", "geronimo1", "localhost");
        String trigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
        BotMessage msg = new BotMessage(this.conn, "#chan", user, 
                trigger + "a testA");
        BotCommandInterpreter instance =
                new BotCommandInterpreter(new BotSpamDetector());

        instance.addListener(new BotCommandEvent("a"), new CommandA(this.s));
        instance.addListener(new BotCommandEvent("b"), new CommandB(this.s));

        this.s.setValue("D");
        instance.run(msg);
        assertEquals("A", this.s.getValue());

        msg.setText(trigger + "b testB");
        instance.run(msg);
        assertEquals("B", this.s.getValue());

        this.s.setValue("C");
        msg.setText(trigger + "g");
        instance.run(msg);
        assertEquals("C", this.s.getValue());

        msg.setText(trigger + "a testA");
        instance.run(msg);
        assertEquals("A", this.s.getValue());
    }

    @Test
    public void testAddLinkListener()
    {
        System.out.println("addLinkListener");   
        BotCommandInterpreter instance = 
                new BotCommandInterpreter(new BotSpamDetector());
        IRCUser user = new IRCUser("gresco", "geronimo", "localhost");
        BotMessage msg = new BotMessage(this.conn, "#chan", user,
                "http://www.abc.com");
        
        instance.addLinkListener(new CommandLink(this.s));

        this.s.setValue("D");
        instance.run(msg);
        assertEquals("Link", this.s.getValue());
    }
    
    class SharedObject 
    {
        String value;
        
        SharedObject() 
        {
            this.value = null;
        }
        
        public void setValue(String val)
        {
            this.value = val;
        }
        
        public String getValue()
        {
            return this.value;
        }
    }

    class CommandA implements BotCommand
    {
        SharedObject a;

        public CommandA(SharedObject a)
        {
            this.a = a;
        }

        public void execute(BotMessage msg)
        {
            this.a.setValue("A");
            System.out.println("A:" + msg.getText());
        }

        public String help() { return null; }
    }

    class CommandB implements BotCommand
    {
        SharedObject b;

        public CommandB(SharedObject a)
        {
            this.b = a;
        }

        public void execute(BotMessage msg)
        {
            this.b.setValue("B");
            System.out.println("B:" + msg.getText());
        }

        public String help() { return null; }
    }

    class CommandLink implements BotCommand
    {
        SharedObject l;

        public CommandLink(SharedObject a)
        {
            this.l = a;
        }

        public void execute(BotMessage msg)
        {
            this.l.setValue("Link");
            System.out.println("Link: " + msg.getText());
        }

        public String help() { return null; }
    }
}