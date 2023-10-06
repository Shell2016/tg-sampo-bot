package ru.michaelshell.sampo_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public record RedisProperties(String host,
                              int port,
                              String password) {
}
