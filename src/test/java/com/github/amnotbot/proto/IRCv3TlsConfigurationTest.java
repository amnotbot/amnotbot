package com.github.amnotbot.proto;

import java.lang.reflect.Field;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.config.BotConfiguration;
import com.github.amnotbot.proto.ircv3.IRCv3BotConnection;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.kitteh.irc.client.library.Client;

import static org.junit.Assert.*;

public class IRCv3TlsConfigurationTest {
    @Test
    public void trustOverrideIsOptInPerServerAndRequiresTls() throws Exception {
        BotConfiguration.setHomeDir("target/test-classes");
        Configuration global = BotConfiguration.getConfig();
        Object account = global.getProperty("account_name");
        Object password = global.getProperty("account_password");
        global.setProperty("account_name", "tls-test");
        global.setProperty("account_password", "tls-test");
        try {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.setProperty("ircv3.sekurnet.server", "localhost");
            config.setProperty("ircv3.sekurnet.port", 6697);
            config.setProperty("ircv3.sekurnet.ssl", true);
            assertClient(config, true, false);
            config.setProperty("ircv3.sekurnet.untrusted-certificates", true);
            assertClient(config, true, true);
            config.setProperty("ircv3.sekurnet.untrusted-certificates", false);
            assertClient(config, true, false);
            config.setProperty("ircv3.sekurnet.untrusted-certificates", true);
            config.setProperty("ircv3.sekurnet.ssl", false);
            assertClient(config, false, false);
        } finally {
            global.setProperty("account_name", account);
            global.setProperty("account_password", password);
        }
    }

    private void assertClient(Configuration config, boolean tls,
            boolean untrusted) throws Exception {
        BotConnection connection = new BotConnectionFactory().createConnection(
                "ircv3", config.subset("ircv3.sekurnet"));
        try {
            Field field = IRCv3BotConnection.class.getDeclaredField("client");
            field.setAccessible(true);
            Client.WithManagement client = (Client.WithManagement) field.get(connection);
            assertEquals(tls, client.isSecureConnection());
            if (untrusted) {
                assertSame(InsecureTrustManagerFactory.INSTANCE,
                        client.getSecureTrustManagerFactory());
            } else {
                assertNotSame(InsecureTrustManagerFactory.INSTANCE,
                        client.getSecureTrustManagerFactory());
            }
        } finally {
            connection.doQuit();
        }
    }
}
