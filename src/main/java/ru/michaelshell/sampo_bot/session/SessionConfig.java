package ru.michaelshell.sampo_bot.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class SessionConfig {

    @Value(value = "${redis.host}")
    private String hostName;
    @Value(value = "${redis.port}")
    private int port;
    @Value(value = "${redis.password}")
    private String password;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        RedisStandaloneConfiguration configuration = factory.getStandaloneConfiguration();
        assert configuration != null;
        configuration.setPassword(password);
        configuration.setPort(port);
        configuration.setHostName(hostName);
        return factory;
    }

    @Bean
    public RedisTemplate<Long, UserSession> redisTemplate() {
        RedisTemplate<Long, UserSession> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
