package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.service.UserService;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.COUPLE_REGISTER_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_INFO;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;


@Component
@RequiredArgsConstructor
public class EventCoupleRegisterHandler implements UpdateHandler {

    private final SendService SendService;
    private final EventService eventService;
    private final UserService userService;
    private final EventSoloRegisterHandler eventSoloRegisterHandler;

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();

        if (name.split(" ").length != 2) {
            SendService.sendWithKeyboard(chatId, "Неверный формат! Нужно два слова, разделенные одним пробелом.", session);
        } else {

            String[] s = name.split(" ");
            String partnerFirstName = s[0].trim();
            String partnerLastName = s[1].trim();
            Long eventId = (Long) session.getAttribute("eventId");
            try {
                userService.registerOnEvent(eventId, userId, partnerFirstName, partnerLastName);
            } catch (DataIntegrityViolationException e) {
                SendService.sendWithKeyboard(chatId, "Ошибка записи! Вы уже записаны!", session);
                return;
            }
            session.removeAttribute("eventId");
            session.removeAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name());

            String eventInfo = (String) session.getAttribute(EVENT_INFO.name());
            session.removeAttribute(EVENT_INFO.name());
            SendService.sendWithKeyboard(chatId, "Успешная запись!\uD83E\uDD73", session);
            eventSoloRegisterHandler.sendDancerListWithButtons(eventInfo, user, chatId);
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
            SendService.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }

        Long eventId = eventService.findEventIdByDto(event).orElseThrow();
        session.setAttribute("eventId", eventId);
        SendService.sendWithKeyboard(chatId, msgText +
                "\n\nВведите имя и фамилию партнера/партнерши (желательно именно в таком порядке)", session);
        session.setAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name(), true);
        session.setAttribute(EVENT_INFO.name(), msgText);
    }


}



