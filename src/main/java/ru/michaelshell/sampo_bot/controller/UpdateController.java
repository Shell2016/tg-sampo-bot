package ru.michaelshell.sampo_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.michaelshell.sampo_bot.model.Response;
import ru.michaelshell.sampo_bot.service.UpdateService;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UpdateController {

    private final UpdateService updateService;

    @PostMapping
    public List<Response> processUpdate(@RequestBody Update update) {
        return updateService.processUpdate(update);
    }
}
