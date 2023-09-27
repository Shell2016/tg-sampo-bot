package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.bot.SendService;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.session.SessionAttribute;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.Optional;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.NOTIFY_ALL;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListButtons;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEventInfoHandler implements UpdateHandler, CallbackHandler {

    private final SendService sendService;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {
        if (AuthUtils.isAdmin(session)) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String msgTxt = message.getText();

            if (Boolean.TRUE.equals((session.getAttribute(NOTIFY_ALL.name())))) {
                log.info("Sending message to all users:\n" + msgTxt);
                for (User user : userService.findAll()) {
                    try {
                        sendService.sendWithKeyboardBottom(user.getId(), msgTxt, session);
                    } catch (Exception e) {
                        log.warn("cannot send message to user: %s  %s  %s"
                                .formatted(user.getUserName(), user.getFirstName(), user.getLastName()));
                    }
                }
                session.removeAttribute(NOTIFY_ALL.name());
            } else {
                sendService.sendWithKeyboardBottom(chatId, "Введите сообщение для отправки всем пользователям:", session);
                session.setAttribute(SessionAttribute.NOTIFY_ALL.name(), true);
            }
        }
    }

    @Override
    public void handleCallback(Update update, Session session) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = callbackQuery.getMessage();
        String msgText = BotUtils.getEventInfo(message.getText());

        EventGetDto eventGetDto = BotUtils.parseEvent(msgText);
        Optional<EventReadDto> optionalEvent = eventService.findBy(eventGetDto);

        if (optionalEvent.isPresent()) {
            EventReadDto event = optionalEvent.get();
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventInfo = """
                    Уровень: %s
                    Время: %s
                    %s
                    """.formatted(event.getName(), time, event.getInfo());
            log.info("Sending event info to all users: " + event);
            for (User user : userService.findAll()) {
                try {
                    sendService.sendWithKeyboardInline(user.getId(), eventInfo, eventListButtons);
                } catch (Exception e) {
                    log.warn("Cannot send event to user: " + user);
                }
            }
        }
    }
}
