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
import com.github.amnotbot.cmd.utils.BotURLConnection;
import com.github.amnotbot.config.BotConfiguration;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * BotTask that reports recent github activity.
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
            
            // Display the commit in channel
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
                BotURLConnection conn = new BotURLConnection(it.next());
                
                JSONArray commits = new JSONArray ( conn.fetchURL() );
                
                this.showAnswer(commits);
            }
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }

    @Override
    public void stop()
    {
        // nothing to do.
    }
}
