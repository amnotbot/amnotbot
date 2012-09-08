package com.github.amnotbot.cmd;


import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.CmdOption;
import com.github.amnotbot.cmd.utils.CmdOptionImp;
import com.github.amnotbot.cmd.utils.CommandOptions;
import com.github.amnotbot.cmd.utils.URLGrabber;
import com.github.amnotbot.cmd.utils.WebPageInfo;
import com.github.amnotbot.cmd.utils.WebPageInfoProxy;
import java.util.Date;

public class DeliciousImp
{
    private BotMessage msg;
    private int maxTagLength;
    private CommandOptions opts;
    private WebPageInfo webPageInfo;
    private DeliciousBookmarks delicious;

    public DeliciousImp(DeliciousBookmarks delicious, BotMessage msg,
            int maxTagLength)
    {
        URLGrabber urlGrabber = new URLGrabber(msg.getText());
        
        this.msg = msg;
        this.delicious = delicious;
        this.maxTagLength = maxTagLength;

        opts = new CommandOptions(msg.getText());

        opts.addOption(new CmdOptionImp("title"));
        opts.addOption(new CmdOptionImp("tags", ","));
        opts.addOption(new CmdOptionImp("comment"));

        this.webPageInfo = new WebPageInfoProxy(urlGrabber.getURL());
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
        return this.webPageInfo.getUrl();
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
        success = this.delicious.addPost(this.webPageInfo.getUrl(),
                title, comment, tags.trim(), new Date());
        if (!success) {
            BotLogger.getDebugLogger().debug("Post failed! :-(");
        }
    }
}
