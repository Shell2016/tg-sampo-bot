package ru.michaelshell.sampo_bot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import ru.michaelshell.sampo_bot.session.UserSession;

@Configuration
@RequiredArgsConstructor
public class SessionConfig {

    private final RedisProperties redisProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        RedisStandaloneConfiguration configuration = factory.getStandaloneConfiguration();
        configuration.setPassword(redisProperties.password());
        configuration.setPort(redisProperties.port());
        configuration.setHostName(redisProperties.host());
        return factory;
    }

    @Bean
    public RedisTemplate<Long, UserSession> redisTemplate() {
        RedisTemplate<Long, UserSession> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
