package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static ru.michaelshell.sampo_bot.util.BotUtils.hasRole;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.registerEventModeButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.roleSelectButtons;

public class EventRegisterHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;

    public EventRegisterHandler(SendServiceImpl sendServiceImpl) {
        this.sendServiceImpl = sendServiceImpl;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (!hasRole(session)) {
            sendServiceImpl.sendWithKeyboard(chatId, "Для продолжения нужно пройти небольшую регистрацию", session, roleSelectButtons);

        } else {
            EventGetDto event = parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                sendServiceImpl.sendWithKeyboard(chatId, "Не удалось обработать запрос", session);
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy  HH:mm", new Locale("ru"));
            String time = event.getTime().format(formatter);
            String eventHeader = """
                    Уровень: %s
                    Время: %s
                    """.formatted(event.getName(), time);
            sendServiceImpl.editWithKeyboard(chatId, messageId, eventHeader, registerEventModeButtons);

        }

    }


}
