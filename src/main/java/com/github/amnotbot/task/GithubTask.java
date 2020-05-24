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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    private final String githubApiUrl = "https://api.github.com";
    private final String[] repos;
    private final int ncommits;
    private final Set<String> storedCommits;
    private boolean firstRun = true;

    public GithubTask() {
        this.repos = BotConfiguration.getConfig().getStringArray("github_repos");
        this.ncommits = BotConfiguration.getConfig().getInt("github_commits", 5);

        this.storedCommits = new HashSet<String>(this.ncommits + 64);
    }

    private List<URL> buildUrls() throws MalformedURLException {
        final List<URL> urls = new LinkedList<URL>();
        String[] userRepo;

        for (final String repo : this.repos) {
            userRepo = repo.split(":");

            final String url = this.githubApiUrl + "/repos/" + userRepo[0] + "/" + userRepo[1] + "/commits";

            urls.add(new URL(url));
        }

        return urls;
    }

    private void showAnswer(final JSONArray commits) throws JSONException {

        final String url = commits.getJSONObject(0).getJSONObject("commit").optString("url");
        final String[] userRepo = url.split("/");
        int i = 0;
        while (i < this.ncommits) {
            final JSONObject commit = commits.getJSONObject(i).getJSONObject("commit");
            final String sha = commit.getJSONObject("tree").optString("sha");

            if (!this.storedCommits.add(sha) || this.firstRun) { ++i; continue; }

            // Display the commit in channel
            for (final String channel : this.getChannels()) {
                this.getConnection().doPrivmsg(channel,
                        "(github) " + userRepo[4] + "/" + userRepo[5] + " - Commit: " + commit.optString("message")
                                + ", " + commit.getJSONObject("author").optString("email") + ", "
                                + commit.getJSONObject("author").optString("date"));
            }

            ++i;
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                BotLogger.getDebugLogger().debug(e);
            }
        }
    }

    @Override
    public void run() {
        try {
            final List<URL> gitUrls = this.buildUrls();

            final Iterator<URL> it = gitUrls.iterator();
            while (it.hasNext()) {
                final BotURLConnection conn = new BotURLConnection(it.next());

                final JSONArray commits = new JSONArray(conn.fetchURL());

                this.showAnswer(commits);
            }
        } catch (final Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
        this.firstRun = false;
    }

    @Override
    public void stop() {
        // nothing to do.
    }
}
