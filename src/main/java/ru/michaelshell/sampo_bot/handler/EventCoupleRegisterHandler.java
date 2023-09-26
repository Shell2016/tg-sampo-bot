package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.michaelshell.sampo_bot.bot.SendService;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.UserService;

import java.util.NoSuchElementException;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.COUPLE_REGISTER_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_INFO;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;


@Component
@RequiredArgsConstructor
public class EventCoupleRegisterHandler implements UpdateHandler {

    private final SendService sendService;
    private final EventService eventService;
    private final UserService userService;
    private final DancerListHandler dancerListHandler;

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();

        if (name.split(" ").length != 2) {
            sendService.sendWithKeyboardBottom(chatId, "Неверный формат! Нужно два слова, разделенные одним пробелом.", session);
        } else {

            String[] s = name.split(" ");
            String partnerFirstName = s[0].trim();
            String partnerLastName = s[1].trim();
            Long eventId = (Long) session.getAttribute("eventId");
            try {
                userService.registerOnEvent(eventId, userId, partnerFirstName, partnerLastName);
            } catch (DataIntegrityViolationException e) {
                sendService.sendWithKeyboardBottom(chatId, "Ошибка записи! Вы уже записаны!", session);
                return;
            }

            session.removeAttribute("eventId");
            session.removeAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name());

            String eventInfo = (String) session.getAttribute(EVENT_INFO.name());
            session.removeAttribute(EVENT_INFO.name());
            sendService.sendWithKeyboardBottom(chatId, "Успешная запись!\uD83E\uDD73", session);
            dancerListHandler.sendDancerListWithButtons(eventInfo, user, chatId);
        }
    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText().trim();
        User user = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto event = parseEvent(msgText);

        if (userService.isAlreadyRegistered(event, user.getId())) {
            sendService.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }

        Long eventId = null;
        try {
            eventId = eventService.findEventIdByDto(event).orElseThrow();

        } catch (NoSuchElementException e) {
            sendService.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Коллективка удалена либо изменена!\n" +
                    "Обновите список");
            return;
        }
        session.setAttribute("eventId", eventId);
        sendService.sendWithKeyboardBottom(chatId, msgText +
                "\n\nВведите имя и фамилию партнера/партнерши (желательно именно в таком порядке)", session);
        session.setAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name(), true);
        session.setAttribute(EVENT_INFO.name(), msgText);
    }


}



