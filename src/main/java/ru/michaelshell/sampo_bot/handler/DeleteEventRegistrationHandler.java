package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.util.BotUtils;

@Component
@RequiredArgsConstructor
public class DeleteEventRegistrationHandler implements UpdateHandler {

    private final SendService sendService;
    private final UserEventService userEventService;

    @Override
    public void handleUpdate(Update update, Session session) {
    }


    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String eventInfo = callbackQuery.getMessage().getText();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        User user = callbackQuery.getFrom();

        EventGetDto eventGetDto = BotUtils.parseEvent(eventInfo);
        if (eventGetDto != null) {
            userEventService.deleteEventRegistration(eventGetDto, user.getId());
            sendService.edit(chatId, messageId, "Запись удалена!");
        }
    }


}
