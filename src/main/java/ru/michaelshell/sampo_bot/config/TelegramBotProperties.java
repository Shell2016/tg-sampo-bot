package ru.michaelshell.sampo_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public record TelegramBotProperties(String username,
                                    String token,
                                    String adminId

) {
}
