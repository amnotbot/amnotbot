package org.knix.amnotbot.cmd;

import org.knix.amnotbot.cmd.utils.*;
import org.knix.amnotbot.*;
import java.util.Date;

public class DeliciousThread extends Thread
{
    private String url;
    private BotMessage msg;
    private int maxTagLength;
    private CommandOptions opts;
    WebPageInfoProxy webPageInfo;
    private DeliciousBookmarks delicious;

    public DeliciousThread(DeliciousBookmarks delicious, BotMessage msg,
            int maxTagLength)
    {
        this.msg = msg;
        this.delicious = delicious;
        this.url = msg.getText().trim().split("\\s+")[0];
        this.maxTagLength = maxTagLength;

        opts = new CommandOptions(msg.getText());

        opts.addOption(new CmdOptionImp("title"));
        opts.addOption(new CmdOptionImp("tags", ","));
        opts.addOption(new CmdOptionImp("comment"));

        this.webPageInfo = new WebPageInfoProxy(this.url);

        start();
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

        String [] kWords = this.webPageInfo.getKeywords();
        for (int i = 0; i < kWords.length; ++i) {
            tmpTags += " " + kWords[i].replace(" ", ".");
        }

        if (tmpTags.trim().length() > 0) {
            tmpTags += " " + this.msg.getUser().getNick();
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

        String title = this.webPageInfo.getTitle();
        if (title != null) return title;
        return this.url;
    }

    private String getComment()
    {
        String comment;

        comment = this.opts.getOption("comment").tokens()[0];
        if (comment != null) return comment;

        comment = this.webPageInfo.getDescription();

        return comment;
    }

    public void run()
    {
        String tags;
        String title;
        String comment;

        this.opts.buildArgs();
        
        tags = this.getTags();
        title = this.getTitle();
        comment = this.getComment();

        Boolean success;
        success = this.delicious.addPost(this.url,
                title, comment, tags.trim(), new Date());
        if (!success) {
            BotLogger.getDebugLogger().debug("Post failed! :-(");
        }
    }
}
