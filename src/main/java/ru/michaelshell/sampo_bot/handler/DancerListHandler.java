package ru.michaelshell.sampo_bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendServiceImpl;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.deleteRegistrationButton;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventRegisterButton;


@Slf4j
public class DancerListHandler implements UpdateHandler {

    private final SendServiceImpl sendServiceImpl;
    private final UserEventService userEventService;
    private final UserService userService;


    public DancerListHandler(SendServiceImpl sendServiceImpl, UserEventService userEventService, UserService userService) {
        this.sendServiceImpl = sendServiceImpl;
        this.userEventService = userEventService;
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

        List<UserEvent> userEvents = userEventService.findUserEventsByEvent(eventGetDto);

        String resultList = buildResultList(text,
                getCoupleList(userEvents),
                getDancerList(userEvents, Role.LEADER),
                getDancerList(userEvents, Role.FOLLOWER));

        if (userService.isAlreadyRegistered(eventGetDto, user.getId())) {
            sendServiceImpl.editWithKeyboard(chatId, messageId, resultList, deleteRegistrationButton);
        } else {
            sendServiceImpl.editWithKeyboard(chatId, messageId, resultList, eventRegisterButton);
        }


    }

    private List<String> getCoupleList(List<UserEvent> userEvents) {
        return userEvents.stream()
                .filter(userEvent -> userEvent.getPartnerFullname() != null)
                .sorted(comparing(UserEvent::getSignedAt))
                .map(userEvent -> {
                    ru.michaelshell.sampo_bot.database.entity.User user = userEvent.getUser();
                    String couple;
                    if (user.getRole() == Role.LEADER) {
                        couple = user.getLastName() + " " + user.getFirstName() + "  -  " +
                                userEvent.getPartnerFullname();
                    } else {
                        couple = userEvent.getPartnerFullname() + "  -  " +
                                user.getLastName() + " " + user.getFirstName();
                    }
                    return couple;
                })
                .collect(Collectors.toList());
    }

    private List<String> getDancerList(List<UserEvent> userEvents, Role role) {
        return userEvents.stream()
                .filter(userEvent -> userEvent.getPartnerFullname() == null)
                .filter(userEvent -> userEvent.getUser().getRole() == role)
                .sorted(comparing(UserEvent::getSignedAt))
                .map(userEvent -> userEvent.getUser().getLastName() + " " + userEvent.getUser().getFirstName())
                .collect(toList());
    }

    private String buildResultList(String text, List<String> coupleList, List<String> leaderList, List<String> followerList) {
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

        return text + "\n\n" + couples + leaders + followers + waitingList;
    }


}
