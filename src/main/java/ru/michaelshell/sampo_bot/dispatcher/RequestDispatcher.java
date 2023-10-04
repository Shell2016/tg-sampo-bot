package ru.michaelshell.sampo_bot.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.handler.*;
import ru.michaelshell.sampo_bot.session.UserSession;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static ru.michaelshell.sampo_bot.session.State.*;

@Component
public class RequestDispatcher {

    private final Map<Class<? extends UpdateHandler>, UpdateHandler> updateHandlers;
    private final Map<Class<? extends CallbackHandler>, CallbackHandler> callbackHandlers;

    @Autowired
    public RequestDispatcher(List<UpdateHandler> updateHandlerList, List<CallbackHandler> callbackHandlerList) {
        this.updateHandlers = updateHandlerList.stream()
                .collect(toMap(UpdateHandler::getClass, Function.identity()));
        this.callbackHandlers = callbackHandlerList.stream()
                .collect(toMap(CallbackHandler::getClass, Function.identity()));
    }

    public void dispatchRequest(Request request) {
        Message message = request.update().getMessage();
        if (message != null && message.hasText() && message.isUserMessage()) {
            String messageText = message.getText();
            if (clearSession(request, messageText)) {
                return;
            }
            authenticateWithUpdate(request);
            processSessionStatus(request);
            processUpdate(request, messageText);
        }

        if (request.update().hasCallbackQuery()) {
            String callbackData = request.update().getCallbackQuery().getData();
            authenticateWithCallback(request);
            processCallback(request, callbackData);
        }
    }

    private boolean clearSession(Request request, String messageText) {
        if ("/clear".equals(messageText)) {
            resolveAndHandleUpdate(ClearSessionHandler.class, request);
            return true;
        }
        return false;
    }

    private void authenticateWithUpdate(Request request) {
        if (!request.session().isAuthenticated()) {
            resolveAndHandleUpdate(RegisterHandler.class, request);
        }
    }

    private void authenticateWithCallback(Request request) {
        if (!request.session().isAuthenticated()) {
            resolveAndHandleCallback(RegisterHandler.class, request);
        }
    }

    private void processSessionStatus(Request request) {
        UserSession session = request.session();
        if (session.getState() == DEFAULT) {
            return;
        }
        if (session.getState() == EVENT_ADD_WAITING_FOR_INFO 
                || session.getState() == EVENT_ADD_WAITING_FOR_DATE 
                || session.getState() == EVENT_ADD_WAITING_FOR_NAME) {
            resolveAndHandleUpdate(EventCreateHandler.class, request);
        }

        if (session.getState() == PROMOTION_WAITING_FOR_USERNAME) {
            resolveAndHandleUpdate(PromotionHandler.class, request);
        }

        if (session.getState() == SET_ROLE_WAITING_FOR_NAME) {
            resolveAndHandleUpdate(RoleSetHandler.class, request);
        }

        if (session.getState() == COUPLE_REGISTER_WAITING_FOR_NAME) {
            resolveAndHandleUpdate(EventCoupleRegisterHandler.class, request);
        }

        if (session.getState() == EVENT_EDIT_WAITING_FOR_NAME) {
            resolveAndHandleUpdate(EventEditTitleHandler.class, request);
        }

        if (session.getState() == EVENT_EDIT_WAITING_FOR_INFO) {
            resolveAndHandleUpdate(EventEditInfoHandler.class, request);
        }

        if (session.getState() == EVENT_EDIT_WAITING_FOR_DATE) {
            resolveAndHandleUpdate(EventEditTimeHandler.class, request);
        }

        if (session.getState() == NOTIFY_ALL) {
            resolveAndHandleUpdate(NotifyAllHandler.class, request);
        }
    }

    private void processUpdate(Request request, String messageText) {
        switch (messageText) {
            case "/start", "/help" -> resolveAndHandleUpdate(StartHandler.class, request);
            case "/promote" -> resolveAndHandleUpdate(PromotionHandler.class, request);
            case "/events", "Список коллективок" -> resolveAndHandleUpdate(EventListHandler.class, request);
            case "Добавить" -> resolveAndHandleUpdate(EventCreateHandler.class, request);
            case "/profile" -> resolveAndHandleUpdate(EditProfileHandler.class, request);
            case "/all" -> resolveAndHandleUpdate(NotifyAllHandler.class, request);
        }
    }

    private void processCallback(Request request, String callbackData) {
        switch (callbackData) {
            case "buttonInfoYes", "buttonInfoNo" -> resolveAndHandleCallback(EventCreateHandler.class, request);
            case "buttonEventDelete", "buttonEventDeleteConfirmation" ->
                    resolveAndHandleCallback(EventDeleteHandler.class, request);
            case "buttonEventRegister" -> resolveAndHandleCallback(EventRegisterHandler.class, request);
            case "buttonLeader", "buttonFollower" -> resolveAndHandleCallback(RoleSetHandler.class, request);
            case "buttonSolo" -> resolveAndHandleCallback(EventSoloRegisterHandler.class, request);
            case "buttonCouple" -> resolveAndHandleCallback(EventCoupleRegisterHandler.class, request);
            case "buttonShowDancersList", "buttonRefresh" -> resolveAndHandleCallback(DancerListHandler.class, request);
            case "buttonDeleteRegistration" -> resolveAndHandleCallback(DeleteEventRegistrationHandler.class, request);
            case "buttonEventEdit" -> resolveAndHandleCallback(EventEditHandler.class, request);
            case "buttonEditEventTitle" -> resolveAndHandleCallback(EventEditTitleHandler.class, request);
            case "buttonEditEventTime" -> resolveAndHandleCallback(EventEditTimeHandler.class, request);
            case "buttonEditEventInfo" -> resolveAndHandleCallback(EventEditInfoHandler.class, request);
            case "buttonSendEventInfo" -> resolveAndHandleCallback(SendToAllEventInfoHandler.class, request);
        }
    }

    private void resolveAndHandleUpdate(Class<? extends UpdateHandler> clazz, Request request) {
        updateHandlers.get(clazz).handleUpdate(request);
    }

    private void resolveAndHandleCallback(Class<? extends CallbackHandler> clazz, Request request) {
        callbackHandlers.get(clazz).handleCallback(request);
    }
}
