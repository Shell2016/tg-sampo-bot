package ru.michaelshell.sampo_bot.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.handler.CallbackHandler;
import ru.michaelshell.sampo_bot.handler.ClearSessionHandler;
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
import ru.michaelshell.sampo_bot.handler.InlineQueryHandler;
import ru.michaelshell.sampo_bot.handler.NotifyAllHandler;
import ru.michaelshell.sampo_bot.handler.PromotionHandler;
import ru.michaelshell.sampo_bot.handler.RegisterHandler;
import ru.michaelshell.sampo_bot.handler.RoleSetHandler;
import ru.michaelshell.sampo_bot.handler.SendToAllEventInfoHandler;
import ru.michaelshell.sampo_bot.handler.StartHandler;
import ru.michaelshell.sampo_bot.handler.UpdateHandler;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.BotUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;
import static ru.michaelshell.sampo_bot.session.State.COUPLE_REGISTER_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.State.DEFAULT;
import static ru.michaelshell.sampo_bot.session.State.EVENT_ADD_WAITING_FOR_DATE;
import static ru.michaelshell.sampo_bot.session.State.EVENT_ADD_WAITING_FOR_INFO;
import static ru.michaelshell.sampo_bot.session.State.EVENT_ADD_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.State.EVENT_EDIT_WAITING_FOR_DATE;
import static ru.michaelshell.sampo_bot.session.State.EVENT_EDIT_WAITING_FOR_INFO;
import static ru.michaelshell.sampo_bot.session.State.EVENT_EDIT_WAITING_FOR_NAME;
import static ru.michaelshell.sampo_bot.session.State.NOTIFY_ALL;
import static ru.michaelshell.sampo_bot.session.State.PROMOTION_WAITING_FOR_USERNAME;
import static ru.michaelshell.sampo_bot.session.State.SET_ROLE_WAITING_FOR_NAME;

@Component
public class RequestDispatcher {

    private static final String REGEX_EVENT_ID = "id:(\\d+)";

    private final Map<Class<? extends UpdateHandler>, UpdateHandler> updateHandlers;
    private final Map<Class<? extends CallbackHandler>, CallbackHandler> callbackHandlers;
    private final Map<Class<? extends InlineQueryHandler>, InlineQueryHandler> inlineQueryHandlers;

    @Autowired
    public RequestDispatcher(List<UpdateHandler> updateHandlerList,
                             List<CallbackHandler> callbackHandlerList,
                             List<InlineQueryHandler> inlineQueryHandlerList) {
        this.updateHandlers = updateHandlerList.stream()
                .collect(toMap(UpdateHandler::getClass, Function.identity()));
        this.callbackHandlers = callbackHandlerList.stream()
                .collect(toMap(CallbackHandler::getClass, Function.identity()));
        this.inlineQueryHandlers = inlineQueryHandlerList.stream()
                .collect(toMap(InlineQueryHandler::getClass, Function.identity()));
    }

    public void dispatchRequest(Request request) {
        Message message = request.update().getMessage();
        if (message != null && message.hasText() && message.isUserMessage()) {
            String messageText = message.getText();
            if (isSessionCleared(request, messageText)) {
                return;
            }
            authenticateWithUpdate(request);
            processSessionStatus(request);
            processUpdate(request, messageText);
        }

        if (message != null && message.hasText() && !message.isUserMessage()) {
            processUpdateFromChat(request);
        }

        if (request.update().hasCallbackQuery()) {
            String callbackData = request.update().getCallbackQuery().getData();
            authenticateWithCallback(request);
            processCallback(request, callbackData);
        }

        if (request.update().hasInlineQuery()) {
            String query = request.update().getInlineQuery().getQuery();
            authenticateWithInlineQuery(request);
            processInlineQuery(request, query);
        }

    }

    private boolean isSessionCleared(Request request, String messageText) {
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

    private void authenticateWithInlineQuery(Request request) {
        if (!request.session().isAuthenticated()) {
            resolveAndHandleInlineQuery(RegisterHandler.class, request);
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
        final String addEventsCommand = BotUtils.EVENT_LIST_COMMAND;
        switch (messageText) {
            case "/help" -> resolveAndHandleUpdate(StartHandler.class, request);
            case "/promote" -> resolveAndHandleUpdate(PromotionHandler.class, request);
            case "/start", "/events", addEventsCommand -> resolveAndHandleUpdate(EventListHandler.class, request);
            case "Добавить" -> resolveAndHandleUpdate(EventCreateHandler.class, request);
            case "/profile" -> resolveAndHandleUpdate(EditProfileHandler.class, request);
            case "/all" -> resolveAndHandleUpdate(NotifyAllHandler.class, request);
            case "/dump" -> resolveAndHandleUpdate(DumpEventsHandler.class, request);
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

    private void processInlineQuery(Request request, String query) {
        switch (query) {
            case "events" -> resolveAndHandleInlineQuery(EventListHandler.class, request);
        }
    }

    private void processUpdateFromChat(Request request) {
        // TODO: 27.03.2024 refactor
        Message message = request.update().getMessage();
        Matcher matcher = Pattern.compile(REGEX_EVENT_ID).matcher(message.getText());
        if (matcher.find()) {
            String eventId = matcher.group(1);
            System.out.println(eventId);
        }
    }

    private void resolveAndHandleUpdate(Class<? extends UpdateHandler> clazz, Request request) {
        updateHandlers.get(clazz).handleUpdate(request);
    }

    private void resolveAndHandleCallback(Class<? extends CallbackHandler> clazz, Request request) {
        callbackHandlers.get(clazz).handleCallback(request);
    }

    private void resolveAndHandleInlineQuery(Class<? extends InlineQueryHandler> clazz, Request request) {
        inlineQueryHandlers.get(clazz).handleInlineQuery(request);
    }
}
