![Java CI with Maven](https://github.com/amnotbot/amnotbot/workflows/Java%20CI%20with%20Maven/badge.svg)

# Amnotbot

Amnotbot is a simple pure-java IRC bot.

## Getting Started

You can visit our [wiki page](https://github.com/amnotbot/amnotbot/wiki) for
instructions on how to get started.

## Authors

* Jimmy Mitchener <jcm@packetpan.org>
* Geronimo Poppino <gpoppino@outlook.com>

## YouTube search

`!yt guitar heroe` posts the first video ranked by relevance, with its URL,
title, duration, publication date (UTC), views, and likes. For example:

```text
https://www.youtube.com/watch?v=abcdefghijk | Guitar Hero | Duration: 4:09 | Published: 2020-02-03 | Views: 12345 | Likes: 678
```

The example is illustrative. Results use the official YouTube Data API v3
and can differ from personalized youtube.com searches. Live and upcoming
videos are labeled; unavailable metadata is shown as `unavailable`, not zero.
Long titles are shortened to keep the reply suitable for IRC.

Enable **YouTube Data API v3** in a Google Cloud project, create an API key,
and set `AMNOTBOT_YOUTUBE_API_KEY` in the bot's environment. See Google's
[setup guide](https://developers.google.com/youtube/v3/getting-started).
No user OAuth login or additional Java dependencies are required.

Existing installations must add these entries to their configuration files
under `~/.amnotbot/` (or their configured home directory), then restart:

In `amnotbot.config`:

```properties
youtube_api_key = ${env:AMNOTBOT_YOUTUBE_API_KEY}
```

In `commands.config` (the doubled backslash is intentional):

```properties
YouTubeCommand = ^!yt(?:\\s+.*)?$
```

Each successful search uses one `search.list` request with `type=video`,
`order=relevance`, and `maxResults=1`, followed by one `videos.list` request
for `snippet,contentDetails,statistics`. Search requests have daily limits;
check your project's quotas and Google's
[quota documentation](https://developers.google.com/youtube/v3/determine_quota_cost).
Missing keys, empty results, and API failures produce concise channel replies.
