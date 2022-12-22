package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static ru.michaelshell.sampo_bot.keyboard.KeyboardUtils.*;


public class EventsHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;

    private final static String MSG = "";

    public EventsHandler(SendServiceImpl sendServiceImpl, EventService eventService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {

        Long chatId = update.getMessage().getChatId();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm", new Locale("ru"));

        List<EventReadDto> events = eventService.findAll();
        if (events.isEmpty()) {
            sendServiceImpl.sendMessageWithKeyboard(chatId, "В данный момент нет коллективок", session);
            return;
        }
        sendServiceImpl.sendMessageWithKeyboard(chatId, "Актуальный список коллективок", session);
        events.forEach(event -> {
            String time = event.getTime().format(dateTimeFormatter);
            String eventInfo = """
                    Уровень: %s
                    Время: %s
                    %s
                    """.formatted(event.getName(), time, event.getInfo());

            sendServiceImpl.sendMessageWithKeyboard(chatId, eventInfo, session, eventListButtons);
        });


    }

    @Override
    public void handleCallback(Update update, Session session) {
    }


}
