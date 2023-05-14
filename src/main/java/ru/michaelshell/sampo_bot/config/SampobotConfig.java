package ru.michaelshell.sampo_bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.michaelshell.sampo_bot.bot.SampoBot;
import ru.michaelshell.sampo_bot.service.SendService;

@Configuration
public class SampobotConfig {

    @Bean
    public SendService sendService(SampoBot sampoBot) {
        return new SendService(sampoBot);
    }
}
