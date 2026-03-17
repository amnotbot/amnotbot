# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

```bash
# Build and package (produces fat JAR via maven-shade-plugin)
mvn package

# Build skipping tests
mvn package -DskipTests

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=BotCommandInterpreterTest

# Run the bot
java -jar target/amnotbot-core-0.0.1-SNAPSHOT.jar
```

## Architecture Overview

Amnotbot is a multi-protocol IRC/XMPP bot built with Java 11 and Maven.

### Core Flow

`Main` reads `amnotbot.config`, creates a `BotImp` per protocol section (e.g., `ircv3`, `xmpp`). Each `BotImp` manages one or more `BotConnection` instances (one per server), automatically reconnecting every 5 seconds if a connection drops.

### Protocol Layer (`proto/`)

`BotConnectionFactory` instantiates the correct connection class based on the protocol prefix in the config key:
- `irc.*` → `IRCBotConnection` (using irclib)
- `ircv3.*` → `IRCv3BotConnection` (using kitteh client-lib)
- `xmpp.*` → `XMPPBotConnection` (using Smack)

Each connection sets up a listener (`IRCv3BotListener`, etc.) that receives incoming messages and dispatches them through `BotCommandInterpreter`.

### Command System

Commands are loaded dynamically via reflection in `BotCommandInterpreterBuilderFile.loadCommands()`:
- `commands.config` maps class names (without package prefix) to regex trigger patterns
- Each key is instantiated as `com.github.amnotbot.cmd.<ClassName>`
- Commands implement `BotCommand` and are registered with a `BotCommandEvent` (which wraps the regex)
- To add a new command: create a class in `cmd/`, add an entry to `commands.config`

### Task System

Periodic tasks work similarly — `BotTaskBuilderFile` reads `tasks.config`, instantiates classes from `com.github.amnotbot.task.*`, and schedules them via `BotTaskManager`. The config value is the period in minutes. Current tasks: `TwitterTask`, `GithubTask`.

### Configuration

On first run, config files are copied from the JAR resources to `~/.amnotbot/`. Config values reference environment variables (e.g., `${env:AMNOTBOT_SERVER}`). The three config files:
- `amnotbot.config` — server connections, API keys, bot settings
- `commands.config` — command class name → trigger regex
- `tasks.config` — task class name → period (minutes)

### Coding Conventions (from HACKING.md)

- 4-space indentation, no tabs; 80-char line limit
- Opening/closing curly braces on new lines for class/method declarations
- Javadoc for all non-private methods/fields
- Comments use `/* */` or `/** */`; `//` only for commenting out code
