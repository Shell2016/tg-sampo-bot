package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static ru.michaelshell.sampo_bot.util.BotUtils.isAdmin;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventListHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy  HH:mm", new Locale("ru"));

        List<EventReadDto> events = eventService.findAll();
        if (events.isEmpty()) {
            sendServiceImpl.sendWithKeyboard(chatId, "В данный момент нет коллективок", session);
            return;
        }
        sendServiceImpl.sendWithKeyboard(chatId, "Актуальный список коллективок", session);

        events.forEach(event -> {
            String time = event.getTime().format(dateTimeFormatter);
            String eventInfo = """
                    Уровень: %s
                    Время: %s
                    %s
                    """.formatted(event.getName(), time, event.getInfo());
            sendEventList(session, chatId, eventInfo);
        });
    }


    private void sendEventList(Session session, Long chatId, String eventInfo) {
        if (isAdmin(session)) {
            sendServiceImpl.sendWithKeyboard(chatId, eventInfo, session, eventListAdminButtons);
        } else {
            sendServiceImpl.sendWithKeyboard(chatId, eventInfo, session, eventListButtons);
        }
    }

    @Override
    public void handleCallback(Update update, Session session) {
    }


}
