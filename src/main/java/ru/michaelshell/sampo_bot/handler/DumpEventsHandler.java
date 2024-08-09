package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.config.GoogleProperties;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.service.EventDumpService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import java.util.List;

import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpEventsHandler implements UpdateHandler {

    private static final String GOOGLE_SPREADSHEETS_BASE_URL = "https://docs.google.com/spreadsheets/d/";
    private final EventDumpService eventDumpService;
    private final GoogleProperties googleProperties;

    @Override
    public List<Response> handleUpdate(Request request) {
        UserSession session = request.session();
        Message message = request.update().getMessage();
        Long chatId = message.getChatId();
        String dumpUrl = GOOGLE_SPREADSHEETS_BASE_URL + googleProperties.getSpreadsheets().getSpreadsheetId();
        if (AuthUtils.isAdmin(session)) {
            try {
                eventDumpService.dumpEvents();
            } catch (RuntimeException e) {
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Error: Не удалось выгрузить данные в гугл-таблицу! " + e.getMessage())
                        .build());
            }
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Произведена успешная выгрузка в таблицу: " + dumpUrl)
                    .build());
        } else {
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(AuthUtils.getBottomKeyboard(session))
                    .chatId(chatId)
                    .message("Нет прав для данной операции!")
                    .build());
        }
    }
}
