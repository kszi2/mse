# MSE

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/kszi2/mse/gradle.yml?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Factions%2Fworkflows%2Fgradle.yml)
![GitHub release (with filter)](https://img.shields.io/github/v/release/kszi2/mse?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Freleases%2Flatest%2F)
![GitHub](https://img.shields.io/github/license/kszi2/mse?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Fblob%2Fturnk%2FLICENSE)

*A kinda reasonable discord bot with unique features, written in kotlin*


> **Note:** this project heavily uses the [Javacord](https://github.com/Javacord/Javacord) library.

## Repo stats

| Progress                                                                                                                                         | Code                                                                                                                                   |
|--------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| ![GitHub issues](https://img.shields.io/github/issues/kszi2/mse?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Fissues)                            | ![GitHub top language](https://img.shields.io/github/languages/top/kszi2/mse?link=https%3A%2F%2Fkotlinlang.org)                        |
| ![GitHub pull requests](https://img.shields.io/github/issues-pr/kszi2/mse?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Fpulls)                   | ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kszi2/mse?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse)  |
| ![GitHub contributors](https://img.shields.io/github/contributors/kszi2/mse?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Fgraphs%2Fcontributors) | ![GitHub all releases](https://img.shields.io/github/downloads/kszi2/mse/total?link=https%3A%2F%2Fgithub.com%2Fkszi2%2Fmse%2Freleases) |

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
