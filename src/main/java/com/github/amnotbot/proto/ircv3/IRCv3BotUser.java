package com.github.amnotbot.proto.ircv3;

import com.github.amnotbot.BotUser;

import org.kitteh.irc.client.library.element.User;

public class IRCv3BotUser implements BotUser {

    private User user;

    public IRCv3BotUser(User user)
    {
        this.user = user;
    }

    @Override
    public String getHost() {
        return this.user.getHost();
    }

    @Override
    public String getNick() {
        return this.user.getNick();
    }

    @Override
    public String getUsername() {
        return this.user.getUserString();
    }
}
