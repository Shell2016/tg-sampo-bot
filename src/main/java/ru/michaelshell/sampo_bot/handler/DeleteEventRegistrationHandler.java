package ru.michaelshell.sampo_bot.handler;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.util.BotUtils;

public class DeleteEventRegistrationHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final UserEventService userEventService;

    public DeleteEventRegistrationHandler(SendServiceImpl sendServiceImpl, UserEventService userEventService) {
        this.sendServiceImpl = sendServiceImpl;
        this.userEventService = userEventService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String text = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        User user = callbackQuery.getFrom();

        EventGetDto eventGetDto = BotUtils.parseEvent(text);
        if (eventGetDto != null) {
            userEventService.deleteEventRegistration(eventGetDto, user.getId());
            sendServiceImpl.edit(chatId, messageId, "Запись удалена!");
        }
    }


}
