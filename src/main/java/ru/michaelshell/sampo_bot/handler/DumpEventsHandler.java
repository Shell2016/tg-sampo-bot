package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.config.GoogleProperties;
import ru.michaelshell.sampo_bot.service.EventDumpService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpEventsHandler implements UpdateHandler {

    private static final String GOOGLE_SPREADSHEETS_BASE_URL = "https://docs.google.com/spreadsheets/d/";
    private final ResponseSender responseSender;
    private final EventDumpService eventDumpService;
    private final GoogleProperties googleProperties;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        Message message = request.update().getMessage();
        Long chatId = message.getChatId();
        String dumpUrl = GOOGLE_SPREADSHEETS_BASE_URL + googleProperties.getSpreadsheets().getSpreadsheetId();
        if (AuthUtils.isAdmin(session)) {
            try {
                eventDumpService.dumpEvents();
            } catch (RuntimeException e) {
                responseSender.sendWithKeyboardBottom(chatId, "Error: Не удалось выгрузить данные в гугл-таблицу! " + e.getMessage(), session);
                return;
            }
            responseSender.sendWithKeyboardBottom(
                    chatId,
                    "Произведена успешная выгрузка в таблицу: " + dumpUrl,
                    session);
        } else {
            responseSender.sendWithKeyboardBottom(chatId, "Нет прав для данной операции!", session);
        }
    }
}
