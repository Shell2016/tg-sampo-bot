package ru.michaelshell.sampo_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import ru.michaelshell.sampo_bot.bot.Request;
import ru.michaelshell.sampo_bot.bot.ResponseSender;
import ru.michaelshell.sampo_bot.dto.EventReadDto;
import ru.michaelshell.sampo_bot.service.EventService;
import ru.michaelshell.sampo_bot.session.UserSession;
import ru.michaelshell.sampo_bot.util.AuthUtils;
import ru.michaelshell.sampo_bot.util.TimeParser;

import java.util.ArrayList;
import java.util.List;

import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListAdminButtons;
import static ru.michaelshell.sampo_bot.util.KeyboardUtils.eventListButtons;

/**
 * Lists active events.
 */
@Component
@RequiredArgsConstructor
public class EventListHandler implements UpdateHandler, InlineQueryHandler {

    private final ResponseSender responseSender;
    private final EventService eventService;

    @Override
    public void handleUpdate(Request request) {
        UserSession session = request.session();
        Long chatId = request.update().getMessage().getChatId();

        List<EventReadDto> events = eventService.findAllSortedByTime();
        if (events.isEmpty()) {
            responseSender.sendWithKeyboardBottom(chatId, "В данный момент нет коллективок", session);
            return;
        }
        responseSender.sendWithKeyboardBottom(chatId, "Актуальный список коллективок", session);

        events.forEach(event -> {
            String time = TimeParser.parseFromTimeToString(event.getTime());
            String eventInfo = """
                    Уровень: %s
                    Время: %s
                    %s
                    """.formatted(event.getName(), time, event.getInfo());
            sendEventList(session, chatId, eventInfo);
        });
    }

    @Override
    public void handleInlineQuery(Request request) {
//        UserSession session = request.session();
        InlineQuery inlineQuery = request.update().getInlineQuery();
//        Long chatId = inlineQuery.getFrom().getId();
//        System.out.println();


        List<InlineQueryResult> results = new ArrayList<>();
        List<EventReadDto> events = eventService.findAllSortedByTime();
        events.forEach(event -> {
//            String time = TimeParser.parseFromTimeToString(event.getTime());
//            String eventInfo = """
//                    Уровень: %s
//                    Время: %s
//                    %s
//                    """.formatted(event.getName(), time, event.getInfo());
            InlineQueryResultArticle article = new InlineQueryResultArticle();
            article.setId(event.getId().toString());
            article.setTitle(event.getName());
            article.setDescription(event.getTime() + "\n" + event.getInfo());
            InputTextMessageContent messageContent = new InputTextMessageContent();
            messageContent.setMessageText(
                    event.getName() + " " + event.getTime() + " id:" + event.getId()
            );
            article.setInputMessageContent(messageContent);
            results.add(article);
        });

        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        answer.setResults(results);
        responseSender.sendInlineQueryAnswer(answer);
    }

    private void sendEventList(UserSession session, Long chatId, String eventInfo) {
        if (AuthUtils.isAdmin(session)) {
            responseSender.sendWithKeyboardInline(chatId, eventInfo, eventListAdminButtons);
        } else {
            responseSender.sendWithKeyboardInline(chatId, eventInfo, eventListButtons);
        }
    }
}
