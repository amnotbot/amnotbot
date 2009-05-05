package org.knix.amnotbot.command;

import org.knix.amnotbot.command.utils.*;
import org.knix.amnotbot.*;
import java.util.Date;

public class DeliciousThread extends Thread
{

    private String url;
    private BotMessage msg;
    private int maxTagLength;    
    private boolean showTitle;
    private CommandOptions opts;
    private BotHTMLParser parser;
    private DeliciousBookmarks delicious;

    public DeliciousThread(DeliciousBookmarks delicious, BotMessage msg,
            int maxTagLength,
            boolean showTitle)
    {
        this.msg = msg;
        this.delicious = delicious;
        this.showTitle = showTitle;
        this.url = msg.getText();
        this.maxTagLength = maxTagLength;

        opts = new CommandOptions(msg.getText());

        opts.addOption(new CmdStringOption("title"));
        opts.addOption(new CmdCommaSeparatedOption("tags"));
        opts.addOption(new CmdStringOption("comment"));

        this.parser = new BotHTMLParser(this.url);

        start();
    }

    private String getPageTags()
    {
        String tags = "";

        String keywords;
        keywords = this.parser.getKeywords();
        if (keywords != null) {
            String[] str = keywords.split(",");
            if (str.length == 1) {
                str = keywords.split(" ");
            }
            for (int i = 0; i < str.length; ++i) {
                tags += " " + str[i].trim().replace(" ", ".");
            }
        }
        return tags;
    }

    private String getTags()
    {
        String tmpTags = "";
        String finalTags = "";
        CmdOption tagOption;

        tagOption = this.opts.getOption("tags");
        if (tagOption.hasValue()) {
            for(String t : tagOption.tokens()) {
                tmpTags += t.replaceAll("\\s", ".");
            }
        }

        tmpTags += this.getPageTags();

        if (tmpTags.trim().length() > 0) {
            tmpTags += " " + this.msg.getText();
        } else {
            tmpTags = this.msg.getUser().getNick();
        }

        String[] str = tmpTags.split(" ");
        for (int i = 0; i < str.length; ++i) {
            if (str[i].length() > this.maxTagLength) continue;
            
            finalTags += " " + str[i].trim();
        }
        return finalTags;
    }

    private String getTitle()
    {
        CmdOption titleOption;

        titleOption = this.opts.getOption("title");
        if (titleOption.hasValue()) {
            return titleOption.tokens()[0];
        }

        String title = this.parser.getTitle();
        if (title != null) return title;
        return this.url;
    }

    private boolean isPageTitle()
    {
        if (!this.opts.getOption("title").hasValue()
                && this.parser.getTitle() != null) return true;
        return false;
    }

    public void run()
    {
        String tags;
        String title;
        String comment;

        this.opts.buildArgs();

        tags = this.getTags();
        title = this.getTitle();
        comment = this.opts.getOption("comment").tokens()[0];

        if (this.showTitle && this.isPageTitle()) {
            this.msg.getConn().doPrivmsg(this.msg.getTarget(), title);
        }

        Boolean success;
        success = this.delicious.addPost(this.url,
                title, comment, tags.trim(), new Date());
        if (!success) {
            BotLogger.getDebugLogger().debug("Post failed! :-(");
        }
    }
}
