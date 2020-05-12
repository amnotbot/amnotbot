package com.github.amnotbot.proto.ircv3;

import java.util.List;

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

    public IRCv3BotConnection(final String server, final int port, List<String> channels, final boolean ssl) {

        String nick = BotConstants.getBotConstants().getNick();
        Configuration config = BotConfiguration.getConfig();

        this.client = Client.builder().nick(nick).server().secure(ssl).host(server).then().build();

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
        if (this.logger != null) {
            this.logger.log(msg);
        }
    }

    public void print(String target, String msg)
    {
        if (this.logger != null) {
            this.logger.log(target.toLowerCase(), msg);
        }
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
        return "";
    }

    @Override
    public String getNick() {
        return this.client.getNick();
    }

}