package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.List;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListButtons;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventListHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();

        List<EventReadDto> events = eventService.findAll();
        if (events.isEmpty()) {
            sendServiceImpl.sendWithKeyboard(chatId, "В данный момент нет коллективок", session);
            return;
        }
        sendServiceImpl.sendWithKeyboard(chatId, "Актуальный список коллективок", session);

        events.forEach(event -> {
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventInfo = """
                    Уровень: %s
                    Время: %s
                    %s
                    """.formatted(event.getName(), time, event.getInfo());
            sendEventList(session, chatId, eventInfo);
        });
    }


    private void sendEventList(Session session, Long chatId, String eventInfo) {
        if (AuthUtils.isAdmin(session)) {
            sendServiceImpl.sendWithKeyboard(chatId, eventInfo, session, eventListAdminButtons);
        } else {
            sendServiceImpl.sendWithKeyboard(chatId, eventInfo, session, eventListButtons);
        }
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }


}
