package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.HAS_ROLE;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.registerEventModeButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.roleSelectButtons;

@Component
@RequiredArgsConstructor
public class EventRegisterHandler implements CallbackHandler {

    private final ResponseSender responseSender;

    @Override
    public void handleCallback(Request request) {
        Session session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (!hasRole(session)) {
            responseSender.sendWithKeyboardInline(chatId, "Для продолжения нужно пройти небольшую регистрацию\uD83E\uDDD0", roleSelectButtons);
        } else {
            EventGetDto event = BotUtils.parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                responseSender.sendWithKeyboardBottom(chatId, "Не удалось обработать запрос", session);
                return;
            }
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventHeader = """
                    Уровень: %s
                    Время: %s
                    """.formatted(event.getName(), time);
            responseSender.editWithKeyboardInline(chatId, messageId, eventHeader, registerEventModeButtons);
        }
    }

    private boolean hasRole(Session session) {
        return session.getAttribute(HAS_ROLE.name()) != null;
    }
}
