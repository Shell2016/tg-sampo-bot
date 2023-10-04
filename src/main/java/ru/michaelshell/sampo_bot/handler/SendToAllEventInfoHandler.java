package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.database.entity.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.Optional;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListButtons;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendToAllEventInfoHandler implements CallbackHandler {

    private final ResponseSender responseSender;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public void handleCallback(Request request) {
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
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
                    responseSender.sendWithKeyboardInline(user.getId(), eventInfo, eventListButtons);
                } catch (Exception e) {
                    log.warn("Cannot send event to user: " + user);
                }
            }
        }
    }
}
