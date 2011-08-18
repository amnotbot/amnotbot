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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
    private String [] repos;
    private int ncommits;
    private String firstCommit = null;
    private HashMap<String, String> newestCommit;
    
    public GithubTask()
    {
        this.repos = 
                BotConfiguration.getConfig().getStringArray("github_repos");
        this.ncommits = 
                BotConfiguration.getConfig().getInt("github_commits", 5);
        
        this.newestCommit = new HashMap<String, String>();
    }

    private LinkedList<URL> buildUrls() throws MalformedURLException
    {
        LinkedList<URL> urls = new LinkedList<URL>();
        String [] userRepo;
        
        for (String repo : this.repos) {
            userRepo = repo.split(":");
        
            String url = this.githubApiUrl + "/repos/" + userRepo[0] + 
                    "/" + userRepo[1] + "/commits";
            
            urls.add(new URL(url));
        }
        
        return urls;
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
        
        String url = 
                commits.getJSONObject(0).getJSONObject("commit").optString("url");
        String [] userRepo = url.split("/");
        while (i < this.ncommits) {
            JSONObject commit = 
                    commits.getJSONObject(i).getJSONObject("commit");
            String sha = commit.getJSONObject("tree").optString("sha");
            
            if (this.seenCommit(fCommit, userRepo[5], sha)) break;
            fCommit = false;
            
            for (String channel : this.getChannels()) {
                this.getConnection().doPrivmsg(channel, "(github) " + 
                        userRepo[4] + "/" +  userRepo[5] + " - Commit: " + 
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
        this.newestCommit.put(userRepo[5], this.firstCommit);
    }
    
    private Boolean seenCommit(Boolean fCommit, String repo, String sha)
    {
        if (fCommit) this.firstCommit = sha;
        if (this.newestCommit.get(repo) == null) return true;
        if (StringUtils.equals(this.newestCommit.get(repo), sha)) return true;
        return false;
    }
    
    @Override
    public void run() 
    {
        try {
            LinkedList<URL> gitUrls;
            gitUrls = this.buildUrls();

            Iterator<URL> it = gitUrls.iterator();
            while(it.hasNext()) {
                URLConnection gitConn;
                gitConn = this.startConnection(it.next());
                
                JSONArray commits = this.makeQuery(gitConn);
                
                this.showAnswer(commits);
            }
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }
    
}
