![Java CI with Maven](https://github.com/amnotbot/amnotbot/workflows/Java%20CI%20with%20Maven/badge.svg)

# Amnotbot

Amnotbot is a Java chat bot with IRCv3, classic IRC, and XMPP connections. It
responds to commands, previews shared links and Spotify URIs, saves bookmarks,
and polls GitHub repositories and a Twitter home timeline.

This reference describes the implementation and bundled configuration in this
repository. External integrations require working credentials and compatible
services; their live availability has not been verified as part of this review.

## Build and run

Use a JDK compatible with Java 11 and Maven. The Maven compiler targets Java 11.

```sh
mvn test
mvn package
```

The executable JAR includes dependencies:

```sh
java -jar target/amnotbot-core-0.0.1-SNAPSHOT.jar
```

Before starting, create `~/.amnotbot` and copy any missing configuration files
from `src/main/resources`: `amnotbot.config`, `commands.config`, and
`tasks.config`. Edit those copies for your installation. The bot also copies
missing files on startup, but its first-run welcome message does **not** stop
execution, so configure it before connecting.

Existing configuration files are preserved when upgrading. Changes to bundled
resources do not update existing installations, and configuration changes require
a restart. Environment variables are resolved through `${env:VARIABLE}` entries;
the bot does not load a `.env` file automatically.

For the bundled IRCv3 connection, set these variables in the environment of the
process or service that launches Java:

| Variable | Purpose |
| --- | --- |
| `AMNOTBOT_SERVER` | IRC server hostname. |
| `AMNOTBOT_SERVER_PORT` | Server port, matching the selected TLS mode. |
| `AMNOTBOT_SERVER_SSL` | `true` for TLS, `false` for plaintext. |
| `AMNOTBOT_CHANNELS` | Channel list; see the multiple-channel note below. |
| `AMNOTBOT_NICK` | Bot nickname. |
| `AMNOTBOT_ACCOUNT_NAME` | SASL account name. |
| `AMNOTBOT_ACCOUNT_PASSWORD` | SASL account password. |

IRCv3 always registers SASL PLAIN authentication with the configured account.
For a classic IRC configuration, NickServ settings are described below. Disable
unused commands and scheduled tasks before starting: missing credentials do not
automatically disable most integrations.

The repository also contains a `Dockerfile` that builds and runs the JAR as the
`amnotbot` user, and a `Procfile` defining the same command as a worker. The
Dockerfile uses a floating `maven:latest` base and assumes `yum` is available;
check that combination before relying on it. Container configuration lives in
`/home/amnotbot/.amnotbot`.

## Commands

Send commands in a channel the bot has joined. The default IRCv3 listener handles
channel messages only, not direct messages. Classic IRC and XMPP also forward
private messages to the interpreter.

Command patterns are case-insensitive regular expressions, evaluated with
substring matching (`find()`). Start explicit commands at the beginning of your
message: most handlers take parameters from everything after the first space, even when a
command matched later in the text. YouTube instead requires `!yt` at the start
and parses its own arguments. Multiple matching handlers run independently, and
reply ordering is not guaranteed.

| Command or message | Handler | Behavior |
| --- | --- | --- |
| `!help` | Built-in interpreter | Lists loaded trigger patterns. |
| `!help weather` | Built-in interpreter | Searches trigger text using a case-insensitive regex and calls each matching handler's help method. |
| `!version` | `VersionCommand` | Prints application name, version, and build revision. |
| `!ddg Java` | `DuckDuckGoSearchCommand` | Queries DuckDuckGo's instant-answer endpoint and prints the heading and abstract URL, or `Not found!`. It does not return a general web-results list. |
| `!yt guitar heroe` | `YouTubeCommand` | Posts the first relevance-ranked YouTube video with URL, title, duration, publication date (UTC), views, and likes. `!yt` alone shows usage. |
| `!g Java documentation` | `GoogleWebSearchCommand` | Uses Google Custom Search and sends the first result's title, URL, and snippet as three messages. |
| `recieve (sp?)` | `GoogleSpellingSearchCommand` | Looks up the word immediately before `(sp?)` using Google Custom Search and prints `correctedQuery` when returned. Use the space shown in this example. |
| `!weather Buenos Aires,AR` | `WeatherCommand` | Looks up current weather by city and optional country code. `!weather London` also works. |
| `!price` | `CryptoCoinPriceCommand` | Queries the Blockchain.com exchange ticker for `BTC-USD`. |
| `!price ETH` | `CryptoCoinPriceCommand` | Uses the supplied base symbol with `USD`. |
| `!price ETH BTC` | `CryptoCoinPriceCommand` | Uses the supplied base and quote symbols, uppercased. |
| `!quote` | `QuoteCommand` | Retrieves a random stored quote, including its channel, submitting nickname, and date. Requires the quote database; see limitations below. |
| `!quote Something worth remembering` | `QuoteCommand` | Stores the text, server, channel, submitting nickname, and timestamp. There is no success reply. |
| `!gpt Explain recursion briefly` | `OpenAICommand` | Sends the prompt to the configured chat model and posts the first response, replacing line breaks with spaces. Each request contains one user message; there is no conversation history. |

Weather output includes available sky description, current and maximum/minimum
temperature, humidity, pressure, rain, cloud cover, sunrise/sunset, and wind speed
and direction. Set `openweather_unit` to `metric` for Celsius and meters/second;
other values select imperial units. The current formatter labels rain with `%`.

Price output includes the symbol pair, last trade price, and price 24 hours ago
(not a percentage change). Its formatter always prefixes prices with `$`, even
when the quote currency is not USD. Use one space between symbols.

### Help limitations

`!help` is a case-sensitive prefix check, unlike ordinary command matching.
`!help <pattern>` accepts a regex, not a command name lookup, and invalid regexes
are not handled. Detailed help is incomplete: version and GPT throw an
unsupported-operation exception; Spotify and price return `null`; quote returns
an empty string. Some older help templates expose raw regexes or missing trigger
values. Use this README as the command reference.

### YouTube search

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

## Automatic message features

These handlers require no `!` command. They run on matching messages, including
messages that also contain explicit commands, subject to the same rate limits.

| Feature | Handler | Behavior |
| --- | --- | --- |
| Web page title | `WebPageInfoCommand` | Fetches the first HTTP(S) URL and replies with `[ page title ]` when a title is available. Page metadata uses an in-memory LRU cache of 128 entries by default. |
| Long URL shortening | `QurlRequestCommand` | Sends the first URL to the hard-coded Qurl HTTP API when its length exceeds `qurl_length` (83 characters by default). Replies with the sender's nickname, shortened URL, and domain when extracted. |
| Bookmark saving | `BookmarkCommand` | Posts the first HTTP(S) URL to the configured bookmark backend and replies `Bookmarked: <url>` after a successful request. Disabled unless both backend URL and token are set. |
| Spotify artist | `SpotifyCommand` | `spotify:artist:<id>` prints artist name and popularity/ranking. |
| Spotify album | `SpotifyCommand` | `spotify:album:<id>` prints album name, artists, release date, and track count. |
| Spotify track | `SpotifyCommand` | `spotify:track:<id>` prints track name, artists, duration, and preview URL. |

Use a lowercase Spotify URI with no trailing text. The parser expects an artist,
album, or track URI; ordinary Spotify HTTPS links go through the URL handlers.
Unsupported URI types receive `Valid types: artist, album or track (second field)`.

The URL handlers process only the **first** URL in a message. The title and
shortening handlers retain older, more restrictive trigger patterns than the
bookmark handler, so their URL coverage can differ. The URL extractor stops at
whitespace or angle brackets and does not strip trailing punctuation.

### Bookmark backend

Set `bookmark_backend_url` to the backend's base URL without a trailing slash,
and `bookmark_jwt_token` to a valid login token. The request is:

```text
POST <bookmark_backend_url>/api/bookmarks
Content-Type: application/json
Cookie: access_token=<bookmark_jwt_token>

{"url":"<first URL in the message>"}
```

Missing, blank, or unresolved `${...}` settings silently disable this feature.
Request failures are logged without a channel error message. The bot does not
refresh tokens or deduplicate submissions locally. Replace an expired token in
the configuration or service environment and restart the bot.

## Scheduled tasks

`tasks.config` maps task classes to positive intervals in **minutes**. Each
connection creates its own task manager. The first execution is scheduled after
one minute, followed by the configured interval. Comment out a task's line with
`#` to disable it; restart after editing.

| Task | Bundled interval | Behavior and configuration |
| --- | --- | --- |
| `GithubTask` | 30 minutes | Polls public repository commits using the unauthenticated GitHub API. Set `github_repos` to comma-separated `owner:repo` entries and `github_commits` to the number inspected per repository (default 5). The first poll records existing entries without announcing them; later polls post unseen entries to all configured channels with repository, commit message, author email, and date. |
| `TwitterTask` | 10 minutes | Polls the authenticated account's home timeline using Twitter4J. The first successful poll records existing statuses; subsequent polls announce unseen statuses as `@screenName: text`, flattening newlines. Requires all four Twitter OAuth settings. |

Task history is in memory and resets when the task is recreated. GitHub currently
uses tree SHAs for deduplication, so different commits with the same tree can be
collapsed; it also assumes at least `github_commits` results are returned. Twitter
stores up to 1,024 hashes of author/text. Its deduplication happens inside the
channel loop, so each new status is currently sent only to the first configured
channel. Both tasks are enabled in the bundled file even without credentials or
repository settings.

## Configuration reference

`~/.amnotbot/amnotbot.config` contains connection and integration settings.
Literal values can replace the environment placeholders in the bundled file.

| Config key | Bundled environment variable or default |
| --- | --- |
| `nick` | `AMNOTBOT_NICK` |
| `account_name`, `account_password` | `AMNOTBOT_ACCOUNT_NAME`, `AMNOTBOT_ACCOUNT_PASSWORD` |
| `nickserv_enabled` | `AMNOTBOT_NICKSERV_ENABLED` |
| `nickserv` | `NICKSERV` |
| `nickserv_password` | `AMNOTBOT_NICKSERV_PASSWORD` |
| `help_trigger` | `!help` |
| `qurl_length` | `83` |
| `webpages_cache_size` | `128` |
| `language`, `country` | `en`, `US` (bundled message/help resources) |
| `google_search_engine_id` | `AMNOTBOT_GOOGLE_SEARCH_ENGINE_ID` |
| `google_search_key` | `AMNOTBOT_GOOGLE_SEARCH_KEY` |
| `openweather_key` | `AMNOTBOT_OPENWEATHER_KEY` |
| `openweather_unit` | `AMNOTBOT_OPENWEATHER_UNIT` (`metric` or `imperial`) |
| `spotify_client_id` | `AMNOTBOT_SPOTIFY_CLIENTID` |
| `spotify_client_secret` | `AMNOTBOT_SPOTIFY_CLIENTSECRET` |
| `youtube_api_key` | `AMNOTBOT_YOUTUBE_API_KEY` |
| `openai_secret_key` | `OPENAI_SECRET_KEY` |
| `openai_model` | `OPENAI_MODEL` (no model default) |
| `bookmark_backend_url` | `AMNOTBOT_BOOKMARK_BACKEND_URL` |
| `bookmark_jwt_token` | `AMNOTBOT_BOOKMARK_JWT_TOKEN` |
| `github_repos` | `AMNOTBOT_GITHUB_REPOS` |
| `github_commits` | `5` |
| `twitter_key`, `twitter_secret` | `AMNOTBOT_TWITTER_KEY`, `AMNOTBOT_TWITTER_SECRET` |
| `twitter_token`, `twitter_token_secret` | `AMNOTBOT_TWITTER_TOKEN`, `AMNOTBOT_TWITTER_TOKEN_SECRET` |

### Connections and multiple channels

Connection keys follow `<protocol>.<connection-name>.<setting>`. Supported
protocol names are `ircv3`, `irc`, and `xmpp`. The bundled `freenode` connection
name is just a label; `AMNOTBOT_SERVER` determines its actual host. Multiple
connection groups can be configured simultaneously and share global command and
integration settings.

IRC/IRCv3 groups accept `server`, `port` (6667 if omitted), `ssl`, and `channels`.
Configure TLS explicitly with the appropriate server port. For multiple channels,
write a literal list in the config:

```properties
ircv3.freenode.channels = #amnotbot, #amnottesting
```

Commons Configuration splits literal comma-separated values when loading the
file. Do not assume a comma-separated value inside one interpolated environment
variable will become multiple list entries; use literal lists for channels and
`github_repos` when configuring multiple values.

Classic IRC sends `IDENTIFY <password>` to `nickserv` when `nickserv_enabled` is
true. It also supports a global `auto_rejoin = true` setting after a kick; this
key is absent from the bundled config, so set it explicitly when using classic
IRC. These features are separate from IRCv3 SASL authentication.

XMPP groups accept `server`, `port` (5222 if omitted), `channels` (room JIDs),
`user`, `password`, and `resource`. The commented example in the bundled config
shows the shape. XMPP supports room and incoming private chat messages; room
joins request no history. This legacy adapter enables compression, disables
SASL, and allows self-signed certificates.

### Enabling, disabling, and changing commands

`~/.amnotbot/commands.config` maps the handler class names listed above to regex
triggers. Comment out a line with `#` to disable the handler. Editing a trigger
changes which messages reach the handler, but does not change its argument
parser. Escape backslashes for the properties format (for example `\\S+`) and
escape commas in regexes as the bundled file does. There is no global command
prefix setting; `help_trigger` only controls help.

### Quote storage

Quote storage reads these environment variables directly:

- `JDBC_DATABASE_URL`, for example `jdbc:postgresql://localhost:5432/amnotbot`.
- `JDBC_DATABASE_USERNAME`.
- `JDBC_DATABASE_PASSWORD`.

The bundled Hibernate configuration selects PostgreSQL and maps `Quote` to
`QUOTES` with `id`, `QUOTE_DATE`, `server`, `channel`, `nick`, and `text` fields.
It does not configure automatic schema creation or provide migrations; provision
a schema compatible with `src/main/java/com/github/amnotbot/hbm/Quote.hbm.xml`.

Random retrieval selects from the entire table, with no server/channel filter.
The implementation uses `order by rand()` despite the PostgreSQL configuration,
and accesses the first result without checking for an empty table. Treat quote
retrieval as requiring database validation/fixes before depending on it.

## Runtime behavior and troubleshooting

- **Rate limiting:** matching messages are limited per connection and target,
  with a three-second minimum gap, a global threshold of ten requests per minute,
  and a per-nickname threshold of three per minute. Rejected messages are silently
  ignored. Automatic URL handlers count too; help bypasses this detector.
- **Long replies:** IRCv3 splits outgoing text into chunks of at most 392 Java
  characters, preferring spaces. Other adapters do not use this splitter.
- **Connections:** classic IRC and XMPP participate in the bot's five-second
  disconnected-connection check. IRCv3 reports itself connected unconditionally
  to that check and relies on its client library's connection lifecycle. Task
  managers are cancelled on IRC/IRCv3 disconnect and recreated on connection.
- **Logging:** Log4j defaults to debug-level console output. Classic IRC/XMPP
  logging creates files under `~/.amnotbot/log/<server>/`; IRCv3 prints timestamped
  raw incoming/outgoing lines to stdout. The Spotify implementation also prints
  its access token to stdout, so logs can contain credentials as well as chat.
- **No reply:** check rate limits, whether the handler is enabled, credentials,
  and console logs. Many API errors are only logged. Google assumes a first
  result or spelling object exists, and weather replies only to valid results.
- **Integration scope:** GPT uses the Chat Completions endpoint with the configured
  model. DuckDuckGo and Qurl use hard-coded HTTP URLs. Provider/API changes may
  require code changes; configuration alone does not guarantee compatibility.

## Development

Run `mvn test` for the Java tests and `mvn package` to build the executable JAR.
Tests cover command interpretation, configuration helpers, URL extraction,
command-option parsing, bookmark HTTP behavior, and YouTube search/formatting; they do not establish that
all external integrations work live.

To add a command, implement `BotCommand` in `com.github.amnotbot.cmd` with a
public no-argument constructor and register it in `commands.config`. To add a
scheduled task, extend `BotTask` in `com.github.amnotbot.task` and register its
interval in `tasks.config`. Existing installations need the same registration in
their `~/.amnotbot` copies. See `HACKING.md` for repository coding conventions.

## Authors

- Jimmy Mitchener <jcm@packetpan.org>
- Geronimo Poppino <gpoppino@outlook.com>
