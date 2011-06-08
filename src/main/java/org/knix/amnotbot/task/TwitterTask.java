package org.knix.amnotbot.task;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.knix.amnotbot.BotLogger;
import org.knix.amnotbot.BotTask;
import org.knix.amnotbot.config.BotConfiguration;
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
            statuses = twitter.getFriendsTimeline();
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
        if (StringUtils.equals(this.newestTweet, hashText)) return true;
        return false;
    }

}
