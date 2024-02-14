package ru.michaelshell.sampo_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.michaelshell.sampo_bot.handler.DancerListHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для выгрузки коллективок в гугл-таблицы.
 */
@Service
@RequiredArgsConstructor
public class EventDumpService {

    private final EventDumpServiceHelper helper;
    private final EventService eventService;
    private final DancerListHandler dancerListHandler;

    public void dumpEvents() {
        eventService.findAllEvents().forEach(event -> {
            String sheetTitle = event.getName() + " " + event.getTime().toString();
            String infoForDump = dancerListHandler.getDancerList(event.getId(), sheetTitle);
            List<List<Object>> values = new ArrayList<>();
            values.add(List.of(infoForDump));
            helper.updateSheet(sheetTitle, values);
        });
    }
}
