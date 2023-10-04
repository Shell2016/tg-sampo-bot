package ru.michaelshell.sampo_bot.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.TimeUnit;

@Component
public class UserSessionService {

    @Value(value = "${session.timeout}")
    private int sessionExpirationTimeInMinutes;
    private final RedisTemplate<Long, UserSession> redisTemplate;
    private final ValueOperations<Long, UserSession> operations;

    public UserSessionService(RedisTemplate<Long, UserSession> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForValue();
    }

    public UserSession getSession(Update update) {
        UserSession session;
        long chatId;
        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        session = operations.get(chatId);
        if (session == null) {
            session = UserSession.builder()
                    .id(chatId)
                    .state(State.DEFAULT)
                    .build();
            operations.set(chatId, session);
        }
        redisTemplate.expire(chatId, sessionExpirationTimeInMinutes, TimeUnit.MINUTES);
        return session;
    }

    public void updateSession(UserSession session) {
        operations.set(session.getId(), session, sessionExpirationTimeInMinutes, TimeUnit.MINUTES);
    }

    public void clearSession(UserSession session) {
        redisTemplate.delete(session.getId());
    }
}
