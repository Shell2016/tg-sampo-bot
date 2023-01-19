package ru.michaelshell.sampo_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SampoBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampoBotApplication.class, args);
    }
}
