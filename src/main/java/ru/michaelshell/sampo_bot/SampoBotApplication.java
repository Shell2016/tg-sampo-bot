package ru.michaelshell.sampo_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SampoBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SampoBotApplication.class, args);
        System.out.println();
    }

}
