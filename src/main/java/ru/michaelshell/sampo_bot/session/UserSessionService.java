package ru.michaelshell.sampo_bot.session;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UserSessionService {

    @Value(value = "${session.timeout}")
    private int sessionExpirationTimeInMinutes;
    private final RedisTemplate<Long, UserSession> redisTemplate;

    public UserSession getSession(Update update) {
        UserSession session;
        long chatId = update.getMessage() != null
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();
        session = redisTemplate.opsForValue().get(chatId);
        if (session == null) {
            session = UserSession.builder()
                    .id(chatId)
                    .state(State.DEFAULT)
                    .build();
            redisTemplate.opsForValue().set(chatId, session);
        }
        redisTemplate.expire(chatId, sessionExpirationTimeInMinutes, TimeUnit.MINUTES);
        return session;
    }

    public void updateSession(UserSession session) {
        redisTemplate.opsForValue().set(session.getId(), session, sessionExpirationTimeInMinutes, TimeUnit.MINUTES);
    }

    public boolean clearSession(UserSession session) {
        return Boolean.TRUE.equals(redisTemplate.delete(session.getId()));
    }
}
