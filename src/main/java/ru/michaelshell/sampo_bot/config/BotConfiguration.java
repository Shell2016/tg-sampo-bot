package ru.michaelshell.sampo_bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.michaelshell.sampo_bot.bot.SampoBot;
import ru.michaelshell.sampo_bot.init.BotInitializer;

@Configuration
public class BotConfiguration {


    @Bean
    public BotInitializer botInitializer(SampoBot sampoBot) {
        return new BotInitializer(sampoBot);
    }

}
