package com.github.amnotbot.task;

import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotTask;
import com.github.amnotbot.config.BotConfiguration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GithubTask extends BotTask
{
    private String githubApiUrl = "https://api.github.com";
    private String repo;
    private String user;
    private int ncommits;
    private String firstCommit = null;
    private String newestCommit = null;
    
    public GithubTask()
    {
        this.repo = BotConfiguration.getConfig().getString("github_repo", 
                "amnotbot");
        this.user = BotConfiguration.getConfig().getString("github_user", 
                "amnotbot");
        this.ncommits = 
                BotConfiguration.getConfig().getInt("github_commits", 5);
    }

    private URL buildUrl() throws MalformedURLException
    {
        String url;
        
        url = this.githubApiUrl + "/repos/" + this.user + "/" + this.repo
                + "/commits";

        return (new URL(url));
    }
    
    private URLConnection startConnection(URL gitUrl) throws IOException
    {
        URLConnection gitConn;

        gitConn = gitUrl.openConnection();
        gitConn.addRequestProperty("Referer", "http://packetpan.org");

        return gitConn;
    }
    
    private JSONArray makeQuery(URLConnection gitConn) 
            throws IOException, JSONException
    {
        BufferedReader reader;
        reader = new BufferedReader(
                        new InputStreamReader(gitConn.getInputStream())
                    );

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return (new JSONArray(builder.toString()));
    }
    
    private void showAnswer(JSONArray commits) throws JSONException
    {
        int i = 0;
        Boolean fCommit = true;
        while (i < this.ncommits) {
            JSONObject commit = 
                    commits.getJSONObject(i).getJSONObject("commit");
            String sha = commit.getJSONObject("tree").optString("sha");
            
            if (this.seenCommit(fCommit, sha)) break;
            fCommit = false;
            
            for (String channel : this.getChannels()) {
                this.getConnection().doPrivmsg(channel, "(github) " + 
                        this.user + "/" +  this.repo + " - Commit: " + 
                        commit.optString("message") + ", " +
                        commit.getJSONObject("author").optString("email") + 
                        ", " +
                        commit.getJSONObject("author").optString("date"));
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                BotLogger.getDebugLogger().debug(e);
            }
            ++i;
        }
        this.newestCommit = this.firstCommit;
    }
    
    private Boolean seenCommit(Boolean fCommit, String sha)
    {
        if (fCommit) this.firstCommit = sha;
        if (this.newestCommit == null) return true;
        if (StringUtils.equals(this.newestCommit, sha)) return true;
        return false;
    }
    
    @Override
    public void run() 
    {
        try {
            URL gitUrl;
            gitUrl = this.buildUrl();

            URLConnection gitConn;
            gitConn = this.startConnection(gitUrl);

            JSONArray commits = this.makeQuery(gitConn);
            
            this.showAnswer(commits);
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }
    
}
