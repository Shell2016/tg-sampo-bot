package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.michaelshell.sampo_bot.bot.SendService;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class DeleteEventRegistrationHandler implements CallbackHandler {

    private final SendService sendService;
    private final UserEventService userEventService;
    private final DancerListHandler dancerListHandler;

    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msgText = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        User user = callbackQuery.getFrom();

        EventGetDto eventGetDto = BotUtils.parseEvent(msgText);
        if (eventGetDto != null) {
            try {
                userEventService.deleteEventRegistration(eventGetDto, user.getId());
                dancerListHandler.editDancerListWithButtons(msgText, user, chatId, messageId);
            } catch (NoSuchElementException e) {
                sendService.edit(chatId, messageId, "Ошибка удаления!");
            }
        }
    }


}
