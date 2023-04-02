package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;

@Component
@RequiredArgsConstructor
public class UpdateHandlerImpl implements UpdateHandler {

    private final StartHandler startHandler;
    private final RegisterHandler registerHandler;
    private final PromotionHandler promotionHandler;
    private final EventListHandler eventListHandler;
    private final EventCreateHandler eventCreateHandler;
    private final EventDeleteHandler eventDeleteHandler;
    private final EventRegisterHandler eventRegisterHandler;
    private final RoleSetHandler roleSetHandler;
    private final EventSoloRegisterHandler eventSoloRegisterHandler;
    private final EventCoupleRegisterHandler eventCoupleRegisterHandler;
    private final DancerListHandler dancerListHandler;
    private final DeleteEventRegistrationHandler deleteEventRegistrationHandler;
    private final EditProfileHandler editProfileHandler;
    private final EventEditHandler eventEditHandler;
    private final EventEditInfoHandler eventEditInfoHandler;
    private final EventEditTimeHandler eventEditTimeHandler;
    private final EventEditTitleHandler eventEditTitleHandler;
    private final NotifyAllHandler notifyAllHandler;
    private final SendEventInfoHandler sendEventInfoHandler;


    @Override
    public void handleUpdate(Update update, Session session) {

        Message message = update.getMessage();

        if (message != null && message.hasText() && message.isUserMessage()) {

            if (!AuthUtils.isAuthenticated(session)) {
                registerHandler.handleUpdate(update, session);
            }

            String messageText = message.getText();
            if ("/clear".equals(messageText)) {
                session.stop();
                return;
            }
            waitingStatusMessageRouter(update, session);


            switch (messageText) {
                case "/start", "/help" -> startHandler.handleUpdate(update, session);
                case "/promote" -> promotionHandler.handleUpdate(update, session);
                case "/events", "Список коллективок" -> eventListHandler.handleUpdate(update, session);
                case "Добавить" -> eventCreateHandler.handleUpdate(update, session);
                case "/profile" -> editProfileHandler.handleUpdate(update, session);
                case "/all" -> notifyAllHandler.handleUpdate(update, session);
            }
        }


        if (update.hasCallbackQuery()) {
            if (!AuthUtils.isAuthenticated(session)) {
                registerHandler.handleCallback(update, session);
            }
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case "buttonInfoYes", "buttonInfoNo" -> eventCreateHandler.handleCallback(update, session);
                case "buttonEventDelete", "buttonEventDeleteConfirmation" -> eventDeleteHandler.handleCallback(update, session);
                case "buttonEventRegister" -> eventRegisterHandler.handleCallback(update, session);
                case "buttonLeader", "buttonFollower" -> roleSetHandler.handleCallback(update, session);
                case "buttonSolo" -> eventSoloRegisterHandler.handleCallback(update, session);
                case "buttonCouple" -> eventCoupleRegisterHandler.handleCallback(update, session);
                case "buttonShowDancersList", "buttonRefresh" -> dancerListHandler.handleCallback(update, session);
                case "buttonDeleteRegistration" -> deleteEventRegistrationHandler.handleCallback(update, session);
                case "buttonEventEdit" -> eventEditHandler.handleCallback(update, session);
                case "buttonEditEventTitle" -> eventEditTitleHandler.handleCallback(update, session);
                case "buttonEditEventTime" -> eventEditTimeHandler.handleCallback(update, session);
                case "buttonEditEventInfo" -> eventEditInfoHandler.handleCallback(update, session);
                case "buttonSendEventInfo" -> sendEventInfoHandler.handleCallback(update, session);
            }
        }

    }

    @Override
    public void handleCallback(Update update, Session session) {
    }

    private void waitingStatusMessageRouter(Update update, Session session) {

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name())) ||
                Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name())) ||
                Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
            eventCreateHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            promotionHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(SET_ROLE_WAITING_FOR_NAME.name()))) {
            roleSetHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name()))) {
            eventCoupleRegisterHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_NAME.name()))) {
            eventEditTitleHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_INFO.name()))) {
            eventEditInfoHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_DATE.name()))) {
            eventEditTimeHandler.handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals((session.getAttribute(NOTIFY_ALL.name())))) {
            notifyAllHandler.handleUpdate(update, session);
        }

    }
}
