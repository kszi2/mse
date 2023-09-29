# MSE
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/kszi2/mse/gradle.yml) ![GitHub release (with filter)](https://img.shields.io/github/v/release/kszi2/mse) ![GitHub](https://img.shields.io/github/license/kszi2/mse)

*A kinda reasonable discord bot with unique features, written in kotlin*

***

> **Note:** this project heavily uses the [Javacord](https://github.com/Javacord/Javacord) library.

## Repo stats

| Progress                                                                     | Code                                                                                      |
|------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| ![GitHub issues](https://img.shields.io/github/issues-raw/kszi2/mse)         | ![GitHub top language](https://img.shields.io/github/languages/top/kszi2/mse)             |
| ![GitHub pull requests](https://img.shields.io/github/issues-pr/kszi2/mse)   | ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kszi2/mse) |
| ![GitHub contributors](https://img.shields.io/github/contributors/kszi2/mse) | ![GitHub all releases](https://img.shields.io/github/downloads/kszi2/mse/total)           |

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
