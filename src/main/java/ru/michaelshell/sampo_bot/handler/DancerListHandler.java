package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.SendService;
import ru.michaelshell.sampo_bot.service.UserEventService;
import ru.michaelshell.sampo_bot.service.UserService;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.deleteRegistrationButton;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventRegisterButton;


@Component
@RequiredArgsConstructor
public class DancerListHandler implements UpdateHandler {

    private final SendService sendService;
    private final UserEventService userEventService;
    private final UserService userService;

    @Override
    public void handleUpdate(Update update, Session session) {
    }

    @Override
    public void handleCallback(Update update, Session session) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String msgText = callbackQuery.getMessage().getText();


        User user = callbackQuery.getFrom();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        // TODO: 01.04.2023 Обновление списка при измененной/удаленной коллективке
        editDancerListWithButtons(msgText, user, chatId, messageId);

    }

    public void editDancerListWithButtons(String msgText, User user, Long chatId, Integer messageId) {

        String eventInfo = getEventInfo(msgText);
        String resultList = getDancerList(eventInfo);

        if ((msgText + "\n").equals(resultList)) {
            return;
        }

        if (userService.isAlreadyRegistered(BotUtils.parseEvent(eventInfo), user.getId())) {
            sendService.editWithKeyboard(chatId, messageId, resultList, deleteRegistrationButton);
        } else {
            sendService.editWithKeyboard(chatId, messageId, resultList, eventRegisterButton);
        }
    }

    private String getEventInfo(String msgText) {
        Matcher matcher = Pattern.compile("^(.+\\n.+)").matcher(msgText);
        String eventInfo = "";
        if (matcher.find()) {
            eventInfo = matcher.group();
        }
        return eventInfo;
    }

    public String getDancerList(String eventInfo) {
        EventGetDto eventGetDto = BotUtils.parseEvent(eventInfo);
        List<UserEvent> userEvents = userEventService.findUserEventsByEvent(eventGetDto);

        return buildResultList(eventInfo,
                printCoupleList(userEvents),
                printDancerList(userEvents, Role.LEADER),
                printDancerList(userEvents, Role.FOLLOWER));
    }


    private List<String> printCoupleList(List<UserEvent> userEvents) {
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

    private List<String> printDancerList(List<UserEvent> userEvents, Role role) {
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

    public void sendDancerListWithButtons(String msgText, User user, Long chatId) {

        String eventInfo = getEventInfo(msgText);
        String resultList = getDancerList(eventInfo);

        if (userService.isAlreadyRegistered(BotUtils.parseEvent(eventInfo), user.getId())) {
            sendService.sendWithKeyboard(chatId, resultList, deleteRegistrationButton);
        } else {
            sendService.sendWithKeyboard(chatId, resultList, eventRegisterButton);
        }
    }


}
