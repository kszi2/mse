# MSE

[![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/kszi2/mse/gradle.yml)](https://github.com/kszi2/mse/actions/workflows/gradle.yml)
[![GitHub release (with filter)](https://img.shields.io/github/v/release/kszi2/mse)](https://github.com/kszi2/mse/releases/latest/)
[![GitHub](https://img.shields.io/github/license/kszi2/mse)](https://github.com/kszi2/mse/blob/turnk/LICENSE)

*A kinda reasonable discord bot with unique features, written in kotlin*


> **Note:** this project heavily uses the [Javacord](https://github.com/Javacord/Javacord) library.

## Repo stats

| Progress                                                                                                                         | Code                                                                                                                                        |
|----------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| [![GitHub issues](https://img.shields.io/github/issues/kszi2/mse)](https://github.com/kszi2/mse/issues)                          | [![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/t/kszi2/mse)](https://github.com/kszi2/mse/commits/turnk) |
| [![GitHub pull requests](https://img.shields.io/github/issues-pr/kszi2/mse)](https://github.com/kszi2/mse/pulls)                 | [![GitHub top language](https://img.shields.io/github/languages/top/kszi2/mse)](https://kotlinlang.org)                                     |
| [![GitHub contributors](https://img.shields.io/github/contributors/kszi2/mse)](https://github.com/kszi2/mse/graphs/contributors) | [![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kszi2/mse)](https://github.com/kszi2/mse)                   |

## Example usage

```kotlin
fun main() {
    bot("Your bot token") {
        // Now you'll have a reference for the Discord Api gateway
        println(createBotInvite().toString())

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        addMessageCreateListener { event ->
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!")
            }
        }
    }
}
```
