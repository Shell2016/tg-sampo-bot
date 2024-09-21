package ru.michaelshell.sampo_bot.dispatcher;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.handler.AuthenticationService;
import ru.michaelshell.sampo_bot.handler.CallbackHandler;
import ru.michaelshell.sampo_bot.handler.DancerListHandler;
import ru.michaelshell.sampo_bot.handler.DeleteEventRegistrationHandler;
import ru.michaelshell.sampo_bot.handler.DumpEventsHandler;
import ru.michaelshell.sampo_bot.handler.EditProfileHandler;
import ru.michaelshell.sampo_bot.handler.EventCoupleRegisterHandler;
import ru.michaelshell.sampo_bot.handler.EventCreateHandler;
import ru.michaelshell.sampo_bot.handler.EventDeleteHandler;
import ru.michaelshell.sampo_bot.handler.EventEditHandler;
import ru.michaelshell.sampo_bot.handler.EventEditInfoHandler;
import ru.michaelshell.sampo_bot.handler.EventEditTimeHandler;
import ru.michaelshell.sampo_bot.handler.EventEditTitleHandler;
import ru.michaelshell.sampo_bot.handler.EventListHandler;
import ru.michaelshell.sampo_bot.handler.EventRegisterHandler;
import ru.michaelshell.sampo_bot.handler.EventSoloRegisterHandler;
import ru.michaelshell.sampo_bot.handler.NotifyAllHandler;
import ru.michaelshell.sampo_bot.handler.PromotionHandler;
import ru.michaelshell.sampo_bot.handler.RoleSetHandler;
import ru.michaelshell.sampo_bot.handler.SendToAllEventInfoHandler;
import ru.michaelshell.sampo_bot.handler.StartHandler;
import ru.michaelshell.sampo_bot.handler.UpdateHandler;
import ru.michaelshell.sampo_bot.model.Request;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.session.UserSessionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class RequestDispatcher {

    private final AuthenticationService authenticationService;
    private final UserSessionService userSessionService;
    private final Map<Class<? extends UpdateHandler>, UpdateHandler> updateHandlers;
    private final Map<Class<? extends CallbackHandler>, CallbackHandler> callbackHandlers;

    public RequestDispatcher(List<UpdateHandler> updateHandlerList,
                             List<CallbackHandler> callbackHandlerList,
                             AuthenticationService authenticationService,
                             UserSessionService userSessionService) {
        this.updateHandlers = updateHandlerList.stream()
                .collect(toMap(UpdateHandler::getClass, Function.identity()));
        this.callbackHandlers = callbackHandlerList.stream()
                .collect(toMap(CallbackHandler::getClass, Function.identity()));
        this.authenticationService = authenticationService;
        this.userSessionService = userSessionService;
    }

    public List<Response> dispatchRequest(Request request) {
        Message message = request.update().getMessage();
        if (message != null && message.hasText() && message.isUserMessage()) {
            String messageText = message.getText();
            if (isSessionCleared(request, messageText)) {
                return Collections.emptyList();
            }
            authenticateWithUpdate(request);
            return processUpdate(request);
        }
        if (request.update().hasCallbackQuery()) {
            String callbackData = request.update().getCallbackQuery().getData();
            authenticateWithCallback(request);
            return processCallback(request, callbackData);
        }
        return Collections.emptyList();
    }

    private boolean isSessionCleared(Request request, String messageText) {
        return "/clear".equals(messageText) && userSessionService.clearSession(request.session());
    }

    private void authenticateWithUpdate(Request request) {
        if (!request.session().isAuthenticated()) {
            authenticationService.authenticateWithUpdate(request);
        }
    }

    private void authenticateWithCallback(Request request) {
        if (!request.session().isAuthenticated()) {
            authenticationService.authenticateWithCallback(request);
        }
    }

    private List<Response> processUpdate(Request request) {
        UserSession session = request.session();
        return switch (session.getState()) {
            case EVENT_ADD_WAITING_FOR_INFO,
                 EVENT_ADD_WAITING_FOR_DATE,
                 EVENT_ADD_WAITING_FOR_NAME -> resolveAndHandleUpdate(EventCreateHandler.class, request);
            case PROMOTION_WAITING_FOR_USERNAME -> resolveAndHandleUpdate(PromotionHandler.class, request);
            case SET_ROLE_WAITING_FOR_NAME -> resolveAndHandleUpdate(RoleSetHandler.class, request);
            case COUPLE_REGISTER_WAITING_FOR_NAME -> resolveAndHandleUpdate(EventCoupleRegisterHandler.class, request);
            case EVENT_EDIT_WAITING_FOR_NAME -> resolveAndHandleUpdate(EventEditTitleHandler.class, request);
            case EVENT_EDIT_WAITING_FOR_INFO -> resolveAndHandleUpdate(EventEditInfoHandler.class, request);
            case EVENT_EDIT_WAITING_FOR_DATE -> resolveAndHandleUpdate(EventEditTimeHandler.class, request);
            case NOTIFY_ALL -> resolveAndHandleUpdate(NotifyAllHandler.class, request);
            case DEFAULT -> processUpdateDefaultState(request);
        };
    }

    private List<Response> processUpdateDefaultState(Request request) {
        String messageText = request.update().getMessage().getText();
        return switch (messageText) {
            case "/help" -> resolveAndHandleUpdate(StartHandler.class, request);
            case "/promote" -> resolveAndHandleUpdate(PromotionHandler.class, request);
            case "/start", "/events", "Список коллективок" -> resolveAndHandleUpdate(EventListHandler.class, request);
            case "Добавить" -> resolveAndHandleUpdate(EventCreateHandler.class, request);
            case "/profile" -> resolveAndHandleUpdate(EditProfileHandler.class, request);
            case "/all" -> resolveAndHandleUpdate(NotifyAllHandler.class, request);
            case "/dump" -> resolveAndHandleUpdate(DumpEventsHandler.class, request);
            default -> Collections.emptyList();
        };
    }

    private List<Response> processCallback(Request request, String callbackData) {
        return switch (callbackData) {
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
            default -> Collections.emptyList();
        };
    }

    private List<Response> resolveAndHandleUpdate(Class<? extends UpdateHandler> clazz, Request request) {
        return updateHandlers.get(clazz).handleUpdate(request);
    }

    private List<Response> resolveAndHandleCallback(Class<? extends CallbackHandler> clazz, Request request) {
        return callbackHandlers.get(clazz).handleCallback(request);
    }
}
