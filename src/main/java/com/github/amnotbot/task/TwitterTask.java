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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotTask;
import com.github.amnotbot.config.BotConfiguration;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author gpoppino
 */
public class TwitterTask extends BotTask
{
    TwitterFactory tf;
    private String newestTweet = null;
    private String firstTweet = null;

    public TwitterTask()
    {
        String key = BotConfiguration.getConfig().getString("twitter_key");
        String secret =
                BotConfiguration.getConfig().getString("twitter_secret");
        String accessToken =
                BotConfiguration.getConfig().getString("twitter_token");
        String tokenSecret =
                BotConfiguration.getConfig().getString("twitter_token_secret");

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey(key)
        .setOAuthConsumerSecret(secret)
        .setOAuthAccessToken(accessToken)
        .setOAuthAccessTokenSecret(tokenSecret);
        this.tf = new TwitterFactory(cb.build());
    }

    @Override
    public void run() 
    {
        Twitter twitter = this.tf.getInstance();

        List<Status> statuses;
        Boolean firstTweet = true;
        try {
            statuses = twitter.getHomeTimeline();
            for (Status status : statuses) {
                String text = StringUtils.replace(status.getText(), "\n", " ");
                if (this.seenTweet(firstTweet, text)) break;
                firstTweet = false;

                for (String channel : this.getChannels()) {
                    this.getConnection().doPrivmsg(channel,
                            "@" + status.getUser().getScreenName()
                                + ": " + text);
                }
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    BotLogger.getDebugLogger().debug(e);
                }
            }
            this.newestTweet = this.firstTweet;
        } catch (TwitterException e) {
            BotLogger.getDebugLogger().debug(e);
        }
    }

    private Boolean seenTweet(Boolean firstTweet, String text)
    {
        MessageDigest md = null;
        byte[] bytesOfMessage = null;
        
        try {
            bytesOfMessage = text.getBytes("UTF-8");
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            BotLogger.getDebugLogger().debug(e);
        } catch (UnsupportedEncodingException e) {
            BotLogger.getDebugLogger().debug(e);
        }
        
        byte[] digest = md.digest(bytesOfMessage);
        BigInteger bigInt = new BigInteger(1, digest);
        String hashText = bigInt.toString(16);
        if (firstTweet) this.firstTweet = hashText;
        if (this.newestTweet == null) return true;
        if (StringUtils.equals(this.newestTweet, hashText)) return true;
        return false;
    }

}
