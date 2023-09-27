package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.bot.SendService;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.HAS_ROLE;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.registerEventModeButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.roleSelectButtons;

@Component
@RequiredArgsConstructor
public class EventRegisterHandler implements CallbackHandler {

    private final SendService sendService;

    @Override
    public void handleCallback(Update update, Session session) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (!hasRole(session)) {
            sendService.sendWithKeyboardInline(chatId, "Для продолжения нужно пройти небольшую регистрацию\uD83E\uDDD0", roleSelectButtons);
        } else {
            EventGetDto event = BotUtils.parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                sendService.sendWithKeyboardBottom(chatId, "Не удалось обработать запрос", session);
                return;
            }
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventHeader = """
                    Уровень: %s
                    Время: %s
                    """.formatted(event.getName(), time);
            sendService.editWithKeyboardInline(chatId, messageId, eventHeader, registerEventModeButtons);
        }
    }

    private boolean hasRole(Session session) {
        return session.getAttribute(HAS_ROLE.name()) != null;
    }
}
