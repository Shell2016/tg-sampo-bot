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
                handlers.get(RegisterHandler.class).handleUpdate(update, session);
            }

            String messageText = message.getText();
            if ("/clear".equals(messageText)) {
                session.stop();
                return;
            }
            waitingStatusMessageRouter(update, session);

            switch (messageText) {
                case "/start", "/help" -> handlers.get(StartHandler.class).handleUpdate(update, session);
                case "/promote" -> handlers.get(PromotionHandler.class).handleUpdate(update, session);
                case "/events", "Список коллективок" ->
                        handlers.get(EventListHandler.class).handleUpdate(update, session);
                case "Добавить" -> handlers.get(EventCreateHandler.class).handleUpdate(update, session);
                case "/profile" -> handlers.get(EditProfileHandler.class).handleUpdate(update, session);
                case "/all" -> handlers.get(NotifyAllHandler.class).handleUpdate(update, session);
            }
        }


        if (update.hasCallbackQuery()) {
            if (!AuthUtils.isAuthenticated(session)) {
                handlers.get(RegisterHandler.class).handleCallback(update, session);
            }
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case "buttonInfoYes", "buttonInfoNo" ->
                        handlers.get(EventCreateHandler.class).handleCallback(update, session);
                case "buttonEventDelete", "buttonEventDeleteConfirmation" ->
                        handlers.get(EventDeleteHandler.class).handleCallback(update, session);
                case "buttonEventRegister" -> handlers.get(EventRegisterHandler.class).handleCallback(update, session);
                case "buttonLeader", "buttonFollower" ->
                        handlers.get(RoleSetHandler.class).handleCallback(update, session);
                case "buttonSolo" -> handlers.get(EventSoloRegisterHandler.class).handleCallback(update, session);
                case "buttonCouple" -> handlers.get(EventCoupleRegisterHandler.class).handleCallback(update, session);
                case "buttonShowDancersList", "buttonRefresh" ->
                        handlers.get(DancerListHandler.class).handleCallback(update, session);
                case "buttonDeleteRegistration" ->
                        handlers.get(DeleteEventRegistrationHandler.class).handleCallback(update, session);
                case "buttonEventEdit" -> handlers.get(EventEditHandler.class).handleCallback(update, session);
                case "buttonEditEventTitle" ->
                        handlers.get(EventEditTitleHandler.class).handleCallback(update, session);
                case "buttonEditEventTime" -> handlers.get(EventEditTimeHandler.class).handleCallback(update, session);
                case "buttonEditEventInfo" -> handlers.get(EventEditInfoHandler.class).handleCallback(update, session);
                case "buttonSendEventInfo" -> handlers.get(SendEventInfoHandler.class).handleCallback(update, session);
            }
        }

    }


    private void waitingStatusMessageRouter(Update update, Session session) {

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_INFO.name())) ||
                Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_DATE.name())) ||
                Boolean.TRUE.equals(session.getAttribute(EVENT_ADD_WAITING_FOR_NAME.name()))) {
            handlers.get(EventCreateHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(PROMOTION_WAITING_FOR_USERNAME.name()))) {
            handlers.get(PromotionHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(SET_ROLE_WAITING_FOR_NAME.name()))) {
            handlers.get(RoleSetHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(COUPLE_REGISTER_WAITING_FOR_NAME.name()))) {
            handlers.get(EventCoupleRegisterHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_NAME.name()))) {
            handlers.get(EventEditTitleHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_INFO.name()))) {
            handlers.get(EventEditInfoHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals(session.getAttribute(EVENT_EDIT_WAITING_FOR_DATE.name()))) {
            handlers.get(EventEditTimeHandler.class).handleUpdate(update, session);
        }

        if (Boolean.TRUE.equals((session.getAttribute(NOTIFY_ALL.name())))) {
            handlers.get(NotifyAllHandler.class).handleUpdate(update, session);
        }

    }
}
