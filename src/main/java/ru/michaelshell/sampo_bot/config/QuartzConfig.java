package ru.michaelshell.sampo_bot.config;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.michaelshell.sampo_bot.job.EventDumpJob;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class QuartzConfig {

    private final GoogleProperties googleProperties;

    @Bean
    JobDetail eventDumpJobDetail() {
        Class<EventDumpJob> jobClazz = EventDumpJob.class;
        return JobBuilder.newJob().ofType(jobClazz)
                .storeDurably()
                .withIdentity(JobKey.jobKey(jobClazz.getCanonicalName(), jobClazz.getClassLoader().getName()))
                .build();
    }

    @Bean
    Trigger eventDumpJobTrigger() {
        String dumpCron = googleProperties.getSpreadsheets().getDumpCron();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(dumpCron)
                .withMisfireHandlingInstructionFireAndProceed();
        return TriggerBuilder.newTrigger()
                .forJob(eventDumpJobDetail())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
