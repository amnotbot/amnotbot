package org.knix.amnotbot.command;

import org.apache.commons.configuration.Configuration;
import org.knix.amnotbot.*;
import org.schwering.irc.lib.IRCUser;
import org.knix.amnotbot.config.BotConfiguration;

public class DeliciousCommand extends BotCommandImp
{

    private boolean showURL;
    private int maxTagLength;
    private DeliciousBookmarks delicious;

    public DeliciousCommand(boolean showURL)
    {
        super("^((http://([a-zA-Z]*.)?[a-zA-Z0-9]+(.[a-z]{2,4})+\\S*).*)",
                "delicious");

        this.showURL = showURL;
        this.delicious = new DeliciousBookmarks();
        Configuration config = BotConfiguration.getConfig();
        this.maxTagLength =
                config.getInteger("delicious_max_tag_length", 30).intValue();
    }

    public void execute(BotConnection con, String chan, IRCUser user, String m)
    {
        new DeliciousThread(this.delicious,
                con, chan, user.getNick(),
                this.getGroup(2).split(" ")[0],
                m, this.maxTagLength, this.showURL);
    }

    public String help()
    {
        String msg;

        msg = "del.icio.us social bookmarking! ";
        msg += "Options: [ title:\"your title\" ";
        msg += "tags:abc,secondtag,thirdtag,def ";
        msg += "comment:\"your comment\" ] ";

        return msg;
    }
}
