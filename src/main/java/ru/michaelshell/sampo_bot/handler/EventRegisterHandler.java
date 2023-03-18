package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.util.TimeParser;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.HAS_ROLE;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.registerEventModeButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.roleSelectButtons;

@Component
@RequiredArgsConstructor
public class EventRegisterHandler implements UpdateHandler {

    private final SendService SendService;

    @Override
    public void handleUpdate(Update update, Session session) {
    }

    @Override
    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (!hasRole(session)) {
            SendService.sendWithKeyboard(chatId, "Для продолжения нужно пройти небольшую регистрацию\uD83E\uDDD0", session, roleSelectButtons);
        } else {
            EventGetDto event = parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                SendService.sendWithKeyboard(chatId, "Не удалось обработать запрос", session);
                return;
            }
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventHeader = """
                    Уровень: %s
                    Время: %s
                    """.formatted(event.getName(), time);
            SendService.editWithKeyboard(chatId, messageId, eventHeader, registerEventModeButtons);
        }
    }

    private boolean hasRole(Session session) {
        return session.getAttribute(HAS_ROLE.name()) != null;
    }


}
