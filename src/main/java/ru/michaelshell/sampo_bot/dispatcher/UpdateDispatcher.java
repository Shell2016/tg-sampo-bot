package ru.michaelshell.sampo_bot.dispatcher;

import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.handler.*;
import ru.michaelshell.sampo_bot.util.AuthUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static ru.michaelshell.sampo_bot.session.SessionAttribute.*;

@Component
public class UpdateDispatcher {

    private final Map<Class<? extends UpdateHandler>, UpdateHandler> handlers;

    @Autowired
    public UpdateDispatcher(List<UpdateHandler> handlersList) {
        this.handlers = handlersList.stream()
                .collect(toMap(UpdateHandler::getClass, Function.identity()));
    }

    public void doDispatch(Update update, Session session) {

        Message message = update.getMessage();

        if (message != null && message.hasText() && message.isUserMessage()) {

            if (!AuthUtils.isAuthenticated(session)) {
                resolveAndHandleUpdate(RegisterHandler.class, update, session);
            }

            String messageText = message.getText();
            if ("/clear".equals(messageText)) {
                session.stop();
                return;
            }
            waitingStatusMessageRouter(update, session);

            switch (messageText) {
                case "/start", "/help" -> resolveAndHandleUpdate(StartHandler.class, update, session);
                case "/promote" -> resolveAndHandleUpdate(PromotionHandler.class, update, session);
                case "/events", "Список коллективок" ->
                        resolveAndHandleUpdate(EventListHandler.class, update, session);
                case "Добавить" -> resolveAndHandleUpdate(EventCreateHandler.class, update, session);
                case "/profile" -> resolveAndHandleUpdate(EditProfileHandler.class, update, session);
                case "/all" -> resolveAndHandleUpdate(NotifyAllHandler.class, update, session);
            }
        }

        if (update.hasCallbackQuery()) {
            if (!AuthUtils.isAuthenticated(session)) {
                resolveAndHandleCallback(RegisterHandler.class, update, session);
            }
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case "buttonInfoYes", "buttonInfoNo" ->
                        resolveAndHandleCallback(EventCreateHandler.class, update, session);
                case "buttonEventDelete", "buttonEventDeleteConfirmation" ->
                        resolveAndHandleCallback(EventDeleteHandler.class, update, session);
                case "buttonEventRegister" -> resolveAndHandleCallback(EventRegisterHandler.class, update, session);
                case "buttonLeader", "buttonFollower" ->
                        resolveAndHandleCallback(RoleSetHandler.class, update, session);
                case "buttonSolo" -> resolveAndHandleCallback(EventSoloRegisterHandler.class, update, session);
                case "buttonCouple" -> resolveAndHandleCallback(EventCoupleRegisterHandler.class, update, session);
                case "buttonShowDancersList", "buttonRefresh" ->
                        resolveAndHandleCallback(DancerListHandler.class, update, session);
                case "buttonDeleteRegistration" ->
                        resolveAndHandleCallback(DeleteEventRegistrationHandler.class, update, session);
                case "buttonEventEdit" -> resolveAndHandleCallback(EventEditHandler.class, update, session);
                case "buttonEditEventTitle" ->
                        resolveAndHandleCallback(EventEditTitleHandler.class, update, session);
                case "buttonEditEventTime" -> resolveAndHandleCallback(EventEditTimeHandler.class, update, session);
                case "buttonEditEventInfo" -> resolveAndHandleCallback(EventEditInfoHandler.class, update, session);
                case "buttonSendEventInfo" -> resolveAndHandleCallback(SendEventInfoHandler.class, update, session);
            }
        }

    }

    private void waitingStatusMessageRouter(Update update, Session session) {

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name())) ||
                Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name())) ||
                Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
            resolveAndHandleUpdate(EventCreateHandler.class, update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            resolveAndHandleUpdate(PromotionHandler.class, update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(SET_ROLE_WAITING_FOR_NAME.name()))) {
            resolveAndHandleUpdate(RoleSetHandler.class, update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name()))) {
            resolveAndHandleUpdate(EventCoupleRegisterHandler.class, update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_NAME.name()))) {
            resolveAndHandleUpdate(EventEditTitleHandler.class, update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_INFO.name()))) {
            resolveAndHandleUpdate(EventEditInfoHandler.class, update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_DATE.name()))) {
            resolveAndHandleUpdate(EventEditTimeHandler.class, update, session);
        }

        if (Boolean.TRUE.equals((session.getAttribute(NOTIFY_ALL.name())))) {
            resolveAndHandleUpdate(NotifyAllHandler.class, update, session);
        }
    }

    private void resolveAndHandleUpdate(Class<? extends UpdateHandler> clazz, Update update, Session session) {
        handlers.get(clazz).handleUpdate(update, session);
    }

    private void resolveAndHandleCallback(Class<? extends UpdateHandler> clazz, Update update, Session session) {
        handlers.get(clazz).handleCallback(update, session);
    }
}
