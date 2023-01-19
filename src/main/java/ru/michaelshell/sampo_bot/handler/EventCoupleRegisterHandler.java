package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.COUPLE_REGISTER_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.EVENT_INFO;
import static ru.michaelshell.sampo_bot.util.BotUtils.parseEvent;

@Slf4j
public class EventCoupleRegisterHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;
    private final UserService userService;


    public EventCoupleRegisterHandler(SendServiceImpl sendServiceImpl, EventService eventService, UserService userService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();
        User user = update.getMessage().getFrom();


        if (name.split(" ").length != 2) {
            sendServiceImpl.sendWithKeyboard(chatId, "Неверный формат! Нужно два слова, разделенные одним пробелом.", session);
        } else {

            String[] s = name.split(" ");
            String partnerFirstName = s[0].trim();
            String partnerLastName = s[1].trim();
            Long eventId = (Long) session.getAttribute("eventId");
            try {
                userService.registerOnEvent(eventId, userId, partnerFirstName, partnerLastName);
            } catch (DataIntegrityViolationException e) {
                sendServiceImpl.sendWithKeyboard(chatId, "Ошибка записи! Вы уже записаны!", session);
                return;
            }
            log.info("Registration couple on event by " + user.getFirstName() + " " + user.getLastName() + ", " +
                    "partner " + partnerLastName + " " + partnerFirstName);
            session.removeAttribute("eventId");
            session.removeAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name());

            String eventInfo = (String) session.getAttribute(EVENT_INFO.name());
            session.removeAttribute(EVENT_INFO.name());


            // TODO: 20.01.2023
            sendServiceImpl.sendWithKeyboard(chatId, "Успешная запись!\uD83E\uDD73", session);
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
            sendServiceImpl.edit(chatId, messageId, "Ошибка записи!\uD83D\uDE31 Вы уже записаны!");
            return;
        }

        Long eventId = eventService.findEventIdByDto(event).orElseThrow();
        session.setAttribute("eventId", eventId);
        sendServiceImpl.edit(chatId, messageId, msgText +
                "\n\nВведите имя и фамилию партнера/партнерши (желательно именно в таком порядке)");
        session.setAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name(), true);
        session.setAttribute(EVENT_INFO.name(), msgText);
    }


}



