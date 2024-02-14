package ru.michaelshell.sampo_bot.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.michaelshell.sampo_bot.service.EventDumpService;

@Component
@RequiredArgsConstructor
public class EventDumpJob implements Job {

    private final EventDumpService service;

    @Override
    public void execute(JobExecutionContext context) {
        service.dumpEvents();
    }
}
