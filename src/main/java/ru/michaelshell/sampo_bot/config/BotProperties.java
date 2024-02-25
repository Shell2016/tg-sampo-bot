package ru.michaelshell.sampo_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public record BotProperties(String username,
                            String token,
                            AdminProperties admin) {

    public record AdminProperties(String username) {
    }
}
