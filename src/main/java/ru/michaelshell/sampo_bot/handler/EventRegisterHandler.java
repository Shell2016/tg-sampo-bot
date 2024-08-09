package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.List;

import static ru.michaelshell.sampo_bot.model.ResponseType.EDIT_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.model.ResponseType.SEND_TEXT_MESSAGE_WITH_KEYBOARD;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.registerEventModeButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.roleSelectButtons;

@Component
@RequiredArgsConstructor
public class EventRegisterHandler implements CallbackHandler {

    @Override
    public List<Response> handleCallback(Request request) {
        UserSession session = request.session();
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        if (!session.hasRole()) {
            return List.of(Response.builder()
                    .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(roleSelectButtons)
                    .chatId(chatId)
                    .message("Чтобы иметь возможность записываться на коллективки," +
                            " нужно один раз пройти регистрацию\uD83E\uDDD0")
                    .build());
        } else {
            EventGetDto event = BotUtils.parseEvent(msgText);
            if (event.getName() == null || event.getTime() == null) {
                return List.of(Response.builder()
                        .type(SEND_TEXT_MESSAGE_WITH_KEYBOARD)
                        .keyboard(AuthUtils.getBottomKeyboard(session))
                        .chatId(chatId)
                        .message("Не удалось обработать запрос")
                        .build());
            }
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventHeader = """
                    Уровень: %s
                    Время: %s
                    """.formatted(event.getName(), time);
            return List.of(Response.builder()
                    .type(EDIT_TEXT_MESSAGE_WITH_KEYBOARD)
                    .keyboard(registerEventModeButtons)
                    .chatId(chatId)
                    .messageId(messageId)
                    .message(eventHeader)
                    .build());
        }
    }
}
