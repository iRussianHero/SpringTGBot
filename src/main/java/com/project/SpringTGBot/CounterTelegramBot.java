package com.project.SpringTGBot;

import config.BotConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CounterTelegramBot {
    @Slf4j
    @Component
    public class CounterTelegramBot extends TelegramLongPollingBot {
        final BotConfig config;

        public CounterTelegramBot(BotConfig config) { this.config = config; }
        @Override
        public String getBotUsername() { return config.getBotName(); }
        @Override
        public String getBotToken() { return config.getToken(); }
        @Override
        public void onUpdateReceived(@NotNull Update update) {}
    }
}
