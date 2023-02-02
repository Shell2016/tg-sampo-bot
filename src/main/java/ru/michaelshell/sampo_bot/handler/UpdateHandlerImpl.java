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

    private final UpdateHandler startHandler;
    private final UpdateHandler registerHandler;
    private final UpdateHandler promotionHandler;
    private final UpdateHandler eventListHandler;
    private final UpdateHandler eventCreateHandler;
    private final UpdateHandler eventDeleteHandler;
    private final UpdateHandler eventRegisterHandler;
    private final UpdateHandler roleSetHandler;
    private final UpdateHandler eventSoloRegisterHandler;
    private final UpdateHandler eventCoupleRegisterHandler;
    private final UpdateHandler dancerListHandler;
    private final UpdateHandler deleteEventRegistrationHandler;
    private final UpdateHandler editProfileHandler;


    @Override
    public void handleUpdate(Update update, Session session) {

        Message message = update.getMessage();

        if (message != null && message.hasText() && message.isUserMessage()) {

            if (!AuthUtils.isAuthenticated(session)) {
                registerHandler.handleUpdate(update, session);
            }

            waitingStatusMessageRouter(update, session);

            String messageText = message.getText();
            switch (messageText) {
                case "/start", "/help" -> startHandler.handleUpdate(update, session);
                case "/promote" -> promotionHandler.handleUpdate(update, session);
                case "/clear" -> session.stop();
                case "/events", "Список коллективок" -> eventListHandler.handleUpdate(update, session);
                case "Добавить" -> eventCreateHandler.handleUpdate(update, session);
                case "/profile" -> editProfileHandler.handleUpdate(update, session);

            }
        }


        if (update.hasCallbackQuery()) {
            if (!AuthUtils.isAuthenticated(session)) {
                registerHandler.handleCallback(update, session);
            }
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case "buttonInfoYes", "buttonInfoNo" -> eventCreateHandler.handleCallback(update, session);
                case "buttonEventDelete" -> eventDeleteHandler.handleCallback(update, session);
                case "buttonEventRegister" -> eventRegisterHandler.handleCallback(update, session);
                case "buttonLeader", "buttonFollower" -> roleSetHandler.handleCallback(update, session);
                case "buttonSolo" -> eventSoloRegisterHandler.handleCallback(update, session);
                case "buttonCouple" -> eventCoupleRegisterHandler.handleCallback(update, session);
                case "buttonShowDancersList" -> dancerListHandler.handleCallback(update, session);
                case "buttonDeleteRegistration" -> deleteEventRegistrationHandler.handleCallback(update, session);
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

    }
}
