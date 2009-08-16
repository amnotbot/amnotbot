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
        this.conn = new DummyConnection();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testAddListener() throws InterruptedException
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
        Thread.sleep(300);
        assertEquals("A", this.s.getValue());

        msg.setText(trigger + "b testB");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("B", this.s.getValue());

        this.s.setValue("C");
        msg.setText(trigger + "g");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("C", this.s.getValue());

        msg.setText(trigger + "a testA");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("A", this.s.getValue());
    }

    @Test
    public void testAddLinkListener() throws InterruptedException
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
        Thread.sleep(300);
        assertEquals("Link", this.s.getValue());
    }

    @Test
    public void testHelp() throws InterruptedException
    {
        System.out.println("testHelp");
        IRCUser user = new IRCUser("gresco1", "geronimo1", "localhost");
        String trigger =
                BotConfiguration.getConfig().getString("command_trigger", ".");
        BotMessage msg = new BotMessage(this.conn, "#chan", user,
                trigger + trigger + "a");
        BotCommandInterpreter instance =
                new BotCommandInterpreter(new BotSpamDetector());

        instance.addListener(new BotCommandEvent("a"), new CommandA(this.s));
        instance.addListener(new BotCommandEvent("b"), new CommandB(this.s));
        instance.addLinkListener(new CommandLink(this.s));

        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpA", this.s.getValue());

        msg.setText(trigger + trigger + "b");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpB", this.s.getValue());

        this.s.setValue("C");
        msg.setText(trigger + trigger + "g");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("C", this.s.getValue());

        msg.setText(trigger + trigger + "a testA");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpA", this.s.getValue());

        this.s.setValue("D");
        msg.setText(trigger + trigger + "url");
        instance.run(msg);
        Thread.sleep(300);
        assertEquals("HelpLink", this.s.getValue());
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

        public String help()
        {
            String h = "HelpA";
            this.a.setValue(h);
            System.out.println("Help: " + h);
            return h;
        }
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

        public String help() 
        {
            String h = "HelpB";
            this.b.setValue(h);
            System.out.println("Help: " + h);
            return h;
        }
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

        public String help() 
        {
            String h = "HelpLink";
            this.l.setValue(h);
            System.out.println("Help: " + h);
            return h;
        }
    }
}