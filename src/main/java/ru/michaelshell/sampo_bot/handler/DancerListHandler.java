package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;
import ru.michaelshell.sampo_bot.util.KeyboardUtils;

import java.util.List;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.deleteRegistrationButton;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventRegisterButton;


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
        List<String> leaderList = eventService.getDancers(eventGetDto, Role.LEADER);
        List<String> followerList = eventService.getDancers(eventGetDto, Role.FOLLOWER);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coupleList.size(); i++) {
            sb.append(i + 1).append(". ").append(coupleList.get(i)).append("\n");
        }
        String couples = "ПАРЫ:\n" + sb;
        sb.setLength(0);

        int max = Integer.max(leaderList.size(), followerList.size());
        int dancerListLength = Integer.min(leaderList.size(), followerList.size());
        for (int i = 0; i < dancerListLength; i++) {
            sb.append(i + 1).append(". ").append(leaderList.get(i)).append("\n");
        }
        String leaders = "\nПАРТНЁРЫ:\n" + sb;
        sb.setLength(0);

        for (int i = 0; i < dancerListLength; i++) {
            sb.append(i + 1).append(". ").append(followerList.get(i)).append("\n");
        }
        String followers = "\nПАРТНЁРШИ:\n" + sb;
        sb.setLength(0);

        List<String> waitList = leaderList.size() == max ? leaderList : followerList;
        for (int i = dancerListLength; i < max; i++) {
            sb.append(i + 1).append(". ").append(waitList.get(i)).append("\n");
        }
        String waitingList = "\nЛИСТ ОЖИДАНИЯ:\n" + sb;

        String resultList = text  + "\n\n" +  couples + leaders + followers + waitingList;

        if (userService.isAlreadyRegistered(eventGetDto, user.getId())) {
            sendServiceImpl.editWithKeyboard(chatId, messageId, resultList, deleteRegistrationButton);
        } else {
            sendServiceImpl.editWithKeyboard(chatId, messageId, resultList, eventRegisterButton);
        }

//        sendServiceImpl.edit(chatId, messageId, resultList);

    }


}
