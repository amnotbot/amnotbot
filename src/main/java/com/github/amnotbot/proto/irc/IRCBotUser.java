package com.github.amnotbot.proto.irc;

import org.schwering.irc.lib.IRCUser;

import com.github.amnotbot.BotUser;

/**
 *
 * @author gpoppino
 */
public class  IRCBotUser implements BotUser
{
    private IRCUser ircUser;

    public IRCBotUser(IRCUser user)
    {
        this.ircUser = user;
    }

    public IRCBotUser(String nick, String username, String host)
    {
        this.ircUser = new IRCUser(nick, username, host);
    }

    public String getHost()
    {
        return this.ircUser.getHost();
    }

    public String getNick()
    {
        return this.ircUser.getNick();
    }

    public String getUsername()
    {
        return this.ircUser.getUsername();
    }

}
