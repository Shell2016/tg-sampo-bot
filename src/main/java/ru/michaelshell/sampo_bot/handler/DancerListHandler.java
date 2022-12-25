package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.List;


@Slf4j
public class DancerListHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final EventService eventService;
    private final UserService userService;


    public DancerListHandler(SendServiceImpl sendServiceImpl, EventService eventService, UserService userService) {
        this.sendServiceImpl = sendServiceImpl;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public void handleUpdate(Update update, Session session) {
    }


    @Override
    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String text = callbackQuery.getMessage().getText();
        User user = callbackQuery.getFrom();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        EventGetDto eventGetDto = BotUtils.parseEvent(text);
        List<String> coupleList = eventService.getCouples(eventGetDto);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coupleList.size(); i++) {
            sb.append(i + 1).append(". ").append(coupleList.get(i)).append("\n");
        }
        String couples = "ОСНОВНОЙ СПИСОК ПАРЫ:\n" + sb;
        String resultList = text  + "\n\n" +  couples;

        // TODO: 25.12.2022 Сделать вывод остальных списков, привязать кнопки снизу для регистрации либо её отмены



        sendServiceImpl.edit(chatId, messageId, resultList);

    }


}
