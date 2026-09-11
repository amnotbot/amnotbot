![Java CI with Maven](https://github.com/amnotbot/amnotbot/workflows/Java%20CI%20with%20Maven/badge.svg)

# Amnotbot

Amnotbot is a simple pure-java IRC bot.

## Getting Started

You can visit our [wiki page](https://github.com/amnotbot/amnotbot/wiki) for
instructions on how to get started.

## IRCv3 TLS certificates

Set these options in `~/.amnotbot/amnotbot.config`.

IRCv3 also accepts `untrusted-certificates` (default `false`). To connect with
TLS to a server using a self-signed certificate, configure that connection:

```properties
ircv3.sekurnet.ssl = true
ircv3.sekurnet.port = 6697
ircv3.sekurnet.untrusted-certificates = true
```

This keeps TLS encryption but disables certificate trust validation for that
connection, accepting any certificate, not only self-signed ones. It has no
effect when `ssl = false`. Classic IRC already accepts untrusted certificates
through its existing trust manager.

## Authors

* Jimmy Mitchener <jcm@packetpan.org>
* Geronimo Poppino <gpoppino@outlook.com>
