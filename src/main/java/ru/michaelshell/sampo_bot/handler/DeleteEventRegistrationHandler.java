package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.model.ResponseType;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class DeleteEventRegistrationHandler implements CallbackHandler {

    private final UserEventService userEventService;
    private final DancerListHandler dancerListHandler;

    public List<Response> handleCallback(Request request) {
        CallbackQuery callbackQuery = request.update().getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        User user = callbackQuery.getFrom();
        EventGetDto eventGetDto = BotUtils.parseEvent(msgText);
        if (eventGetDto != null) {
            try {
                userEventService.deleteEventRegistration(eventGetDto, user.getId());
                return dancerListHandler.getDancerListWithButtonsForEdit(msgText, user, chatId, messageId);
            } catch (NoSuchElementException e) {
                return List.of(Response.builder()
                        .type(ResponseType.EDIT_TEXT_MESSAGE)
                        .chatId(chatId)
                        .messageId(messageId)
                        .message("Ошибка удаления!")
                        .build());
            }
        }
        return Collections.emptyList();
    }
}
