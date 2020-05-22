package com.github.amnotbot.proto.ircv3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotConstants;
import com.github.amnotbot.BotLogger;
import com.github.amnotbot.config.BotConfiguration;

import org.apache.commons.configuration.Configuration;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.auth.SaslPlain;

public class IRCv3BotConnection implements BotConnection {

    private Client client = null;
    private BotLogger logger;

    public IRCv3BotConnection(final String host, final int port, List<String> channels, final boolean ssl) {

        String nick = BotConstants.getBotConstants().getNick();
        Configuration config = BotConfiguration.getConfig();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.client = Client.builder().nick(nick).server().host(host).secure(ssl).then()
                        .listeners()
                        .input(line -> System.out.println(sdf.format(new Date()) + " <- " + line))
                        .output(line -> System.out.println(sdf.format(new Date()) + " -> " + line))
                        .exception(Throwable::printStackTrace).then().build();

        this.client.getAuthManager().addProtocol(new SaslPlain(this.client, config.getString("account_name"),
            config.getString("account_password")));

        this.client.getEventManager().registerEventListener(new IRCv3BotListener(this, channels));

        channels.stream().forEach(c -> this.client.addChannel(c));
    }

    @Override
    public void connect() {
        this.client.connect();
    }

    @Override
    public void doPrivmsg(final String target, final String msg) {
        this.client.sendMessage(target, msg);
    }

    @Override
    public void doQuit() {
        this.client.shutdown("Ciao!");
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    public void print(String msg)
    {
        // Nothing to do. We have an output listener... see the constructor.
    }

    public void print(String target, String msg)
    {
        // Nothing to do. We have an output listener... see the constructor.
    }

    @Override
    public void setBotLogger(final BotLogger logger) {
        this.logger = logger;
    }

    @Override
    public BotLogger getBotLogger() {
        return this.logger;
    }

    @Override
    public String getHost() {
        String host = "";
        try {
            host = client.getServerInfo().getAddress().get();
        } catch(NoSuchElementException e) {
                System.err.println(e);
        }
       return host;
    }

    @Override
    public String getNick() {
        return this.client.getNick();
    }

}
