/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.amnotbot.task;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotTask;
import com.github.amnotbot.config.BotConfiguration;
import org.apache.commons.lang.StringUtils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author gpoppino
 */
public class TwitterTask extends BotTask
{
    boolean firstRun = true;
    TwitterStream twitterStream;

    public TwitterTask()
    {
        final String key = BotConfiguration.getConfig().getString("twitter_key");
        final String secret =
                BotConfiguration.getConfig().getString("twitter_secret");
        final String accessToken =
                BotConfiguration.getConfig().getString("twitter_token");
        final String tokenSecret =
                BotConfiguration.getConfig().getString("twitter_token_secret");

        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey(key)
        .setOAuthConsumerSecret(secret)
        .setOAuthAccessToken(accessToken)
        .setOAuthAccessTokenSecret(tokenSecret);

        final TwitterStreamFactory tsf = new TwitterStreamFactory(cb.build());
        this.twitterStream = tsf.getInstance();

        final StatusListener listener = new TwitterTaskUserStreamListener(this);
        this.twitterStream.addListener(listener);
    }

    @Override
    public void run()
    {
        if (this.firstRun) {
            this.twitterStream.sample();
            this.firstRun = false;
        }
    }

    public void stop()
    {
        this.twitterStream.cleanUp();
        this.twitterStream.shutdown();
    }

    private class TwitterTaskUserStreamListener extends UserStreamAdapter
    {
        TwitterTask task;

        TwitterTaskUserStreamListener(final TwitterTask task)
        {
            this.task = task;
        }

        @Override
        public void onStatus(final Status status)
        {
            final String text = StringUtils.replace(status.getText(), "\n", " ");

            for (final String channel : this.task.getChannels()) {
                this.task.getConnection().doPrivmsg(channel,
                        "@" + status.getUser().getScreenName()
                                + ": " + text);
            }
        }

        @Override
        public void onException(final Exception e)
        {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
    }
}
