package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.michaelshell.sampo_bot.bot.SendService;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.UserEvent;
import ru.michaelshell.sampo_bot.dto.EventGetDto;
import ru.michaelshell.sampo_bot.service.*;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.List;

import static java.util.Comparator.comparing;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.deleteRegistrationButton;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventRegisterButton;


@Component
@RequiredArgsConstructor
public class DancerListHandler implements UpdateHandler {

    private final SendService sendService;
    private final UserEventService userEventService;
    private final UserService userService;
    private final EventService eventService;

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

        editDancerListWithButtons(msgText, user, chatId, messageId);
    }

    public void editDancerListWithButtons(String msgText, User user, Long chatId, Integer messageId) {

        String eventInfo = BotUtils.getEventInfo(msgText);
        EventGetDto eventGetDto = BotUtils.parseEvent(eventInfo);
        if (eventService.findEventIdByDto(eventGetDto).isEmpty()) {
            sendService.edit(chatId, messageId, "Ошибка обновления. Обновите список коллективок.");
            return;
        }

        String resultList = getDancerList(eventInfo);

        if ((msgText + "\n").equals(resultList)) {
            return;
        }


        if (userService.isAlreadyRegistered(eventGetDto, user.getId())) {
            sendService.editWithKeyboardInline(chatId, messageId, resultList, deleteRegistrationButton);
        } else {
            sendService.editWithKeyboardInline(chatId, messageId, resultList, eventRegisterButton);
        }
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
                .toList();
    }

    private List<String> printDancerList(List<UserEvent> userEvents, Role role) {
        return userEvents.stream()
                .filter(userEvent -> userEvent.getPartnerFullname() == null)
                .filter(userEvent -> userEvent.getUser().getRole() == role)
                .sorted(comparing(UserEvent::getSignedAt))
                .map(userEvent -> userEvent.getUser().getLastName() + " " + userEvent.getUser().getFirstName())
                .toList();
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

        String eventInfo = BotUtils.getEventInfo(msgText);
        String resultList = getDancerList(eventInfo);

        if (userService.isAlreadyRegistered(BotUtils.parseEvent(eventInfo), user.getId())) {
            sendService.sendWithKeyboardInline(chatId, resultList, deleteRegistrationButton);
        } else {
            sendService.sendWithKeyboardInline(chatId, resultList, eventRegisterButton);
        }
    }


}
