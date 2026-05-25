# ExoStats

A lightweight and elegant Minecraft plugin for Paper (1.21) that provides a beautiful, interactive GUI for viewing comprehensive player statistics.

## Features

- **Interactive GUI:** Type `/stats` to open a clean, visually appealing inventory interface displaying your or another player's statistics.
- **Detailed Player Info:** View important stats including Level & XP, Kills, Deaths, KDR (Kill-Death Ratio), Playtime, Ping (Latency), and Economy Balance.
- **Join Date Tracking:** The player head icon displays precisely when the player first joined the server.
- **PlaceholderAPI Integration:** Seamlessly displays formatted economy balances and coins using PlaceholderAPI (`$%formatted_balance%` and `%coins_balance%`).
- **Modern API:** Built specifically for Paper 1.21 (Java 21), utilizing the Kyori Adventure Component API for optimal performance and forward compatibility.

## Commands

- `/stats` - View your own statistics.
- `/stats <player>` - View the statistics of another specific player.

## Requirements

- **Server Software:** Paper 1.21 (or compatible forks)
- **Java:** Java 21 or higher
- **Dependencies:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (Soft-depend, used for economy balance formatting)

## Installation

1. Download the latest `ExoStats-1.0.jar` (or compile it yourself using Maven).
2. Place the jar file into your server's `plugins/` directory.
3. (Optional) Install PlaceholderAPI and required economy expansions if you want balances to display properly.
4. Restart your server.

## Building from Source

This project uses Maven. To build the plugin from source, ensure you have JDK 21 installed, then run:

```bash
git clone https://github.com/yourusername/ExoStats.git
cd ExoStats
mvn clean package
```

The compiled jar will be generated in the `target/` directory.
