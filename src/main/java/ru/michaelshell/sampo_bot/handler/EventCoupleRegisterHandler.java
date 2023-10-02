package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.NoSuchElementException;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.COUPLE_REGISTER_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_INFO;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;


@Component
@RequiredArgsConstructor
public class EventCoupleRegisterHandler implements UpdateHandler, CallbackHandler {

    public static final String EVENT_ID = "eventId";

    private final ResponseSender responseSender;
    private final EventService eventService;
    private final UserService userService;
    private final DancerListHandler dancerListHandler;

    @Override
    public void handleUpdate(Request request) {

        Long chatId = request.update().getMessage().getChatId();
        String name = request.update().getMessage().getText();
        Long userId = request.update().getMessage().getFrom().getId();
        User user = request.update().getMessage().getFrom();

        if (name.split(" ").length != 2) {
            responseSender.sendWithKeyboardBottom(chatId, "Неверный формат! Нужно два слова, разделенные одним пробелом.", request.session());
        } else {

            String[] s = name.split(" ");
            String partnerFirstName = s[0].trim();
            String partnerLastName = s[1].trim();
            Long eventId = (Long) request.session().getAttribute(EVENT_ID);
            try {
                userService.registerOnEvent(eventId, userId, partnerFirstName, partnerLastName);
            } catch (DataIntegrityViolationException e) {
                responseSender.sendWithKeyboardBottom(chatId, "Ошибка записи! Вы уже записаны!", request.session());
                return;
            }

            request.session().removeAttribute(EVENT_ID);
            request.session().removeAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name());

            String eventInfo = (String) request.session().getAttribute(EVENT_INFO.name());
            request.session().removeAttribute(EVENT_INFO.name());
            responseSender.sendWithKeyboardBottom(chatId, "Успешная запись!\uD83E\uDD73", request.session());
            dancerListHandler.sendDancerListWithButtons(eventInfo, user, chatId);
        }
    }

    @Override
    public void handleCallback(Request request) {

        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText().trim();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto event = parseEvent(msgText);

        if (userService.isAlreadyRegistered(event, user.getId())) {
            responseSender.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }

        Long eventId;
        try {
            eventId = eventService.findEventIdByDto(event).orElseThrow();

        } catch (NoSuchElementException e) {
            responseSender.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Коллективка удалена либо изменена!\n" +
                    "Обновите список");
            return;
        }
        request.session().setAttribute(EVENT_ID, eventId);
        responseSender.sendWithKeyboardBottom(chatId, msgText +
                "\n\nВведите имя и фамилию партнера/партнерши (желательно именно в таком порядке)", request.session());
        request.session().setAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name(), true);
        request.session().setAttribute(EVENT_INFO.name(), msgText);
    }
}
