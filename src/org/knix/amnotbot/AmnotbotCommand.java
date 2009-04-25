package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

public interface AmnotbotCommand
{

    public boolean matches(String msg);

    public void execute(BotConnection con, String chan, IRCUser user, String msg);

    public String getKeywords();

    public void setKeywords(String keywords);

    public String help();
}
