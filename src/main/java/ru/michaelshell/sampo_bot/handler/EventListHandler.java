package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.List;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListButtons;

@Component
@RequiredArgsConstructor
public class EventListHandler implements UpdateHandler {

    private final ResponseSender responseSender;
    private final EventService eventService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        Long chatId = request.update().getMessage().getChatId();

        List<EventReadDto> events = eventService.findAll();
        if (events.isEmpty()) {
            responseSender.sendWithKeyboardBottom(chatId, "В данный момент нет коллективок", session);
            return;
        }
        responseSender.sendWithKeyboardBottom(chatId, "Актуальный список коллективок", session);

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

    private void sendEventList(UserSession session, Long chatId, String eventInfo) {
        if (AuthUtils.isAdmin(session)) {
            responseSender.sendWithKeyboardInline(chatId, eventInfo, eventListAdminButtons);
        } else {
            responseSender.sendWithKeyboardInline(chatId, eventInfo, eventListButtons);
        }
    }
}
