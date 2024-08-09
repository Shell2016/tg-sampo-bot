package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventListHandler implements UpdateHandler {

    private final EventService eventService;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        Long chatId = request.update().getMessage().getChatId();

        List<EventReadDto> events = eventService.findAllSortedByTime();
        if (events.isEmpty()) {
            return List.of(Response.builder()
                    .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .chatId(chatId)
                    .message("В данный момент нет коллективок")
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .build());
        }
        List<Response> responseList = new ArrayList<>();
        responseList.add(Response.builder()
                .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                .chatId(chatId)
                .message("Актуальный список коллективок")
                .keyboard(AuthUtils.getBottomKeyboard(session))
                .build());

        events.forEach(event -> {
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventInfo = """
                    Уровень: %s
                    Время: %s
                    %s
                    """.formatted(event.getName(), time, event.getInfo());
            responseList.add(Response.builder()
                    .type(ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getInlineKeyboard(session))
                    .chatId(chatId)
                    .message(eventInfo)
                    .build());
        });
        return responseList;
    }
}
