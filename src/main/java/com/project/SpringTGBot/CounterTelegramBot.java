package com.project.SpringTGBot;

import components.Buttons;
import config.BotConfig;
import database.User;
import database.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static components.BotCommands.BotCommands.HELP_TEXT;

public class CounterTelegramBot {
    @Slf4j
    @Component
    public class CounterTelegramBot extends TelegramLongPollingBot implements BotCommands {
        final BotConfig config;

        public CounterTelegramBot(BotConfig config) {
            this.config = config;
            try {
                this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
            } catch (TelegramApiException e){
                log.error(e.getMessage());
            }
        }

        @Override
        public String getBotUsername() {
            return config.getBotName();
        }

        @Override
        public String getBotToken() {
            return config.getToken();
        }

        @Override
        public void onUpdateReceived(@NotNull Update update) {
            long chatId = 0;
            long userId = 0; //это нам понадобится позже
            String userName = null;
            String receivedMessage;

            //если получено сообщение текстом
            if(update.hasMessage()) {
                chatId = update.getMessage().getChatId();
                userId = update.getMessage().getFrom().getId();
                userName = update.getMessage().getFrom().getFirstName();

                if (update.getMessage().hasText()) {
                    receivedMessage = update.getMessage().getText();
                    botAnswerUtils(receivedMessage, chatId, userName);
                }

                //если нажата одна из кнопок бота
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
                userId = update.getCallbackQuery().getFrom().getId();
                userName = update.getCallbackQuery().getFrom().getFirstName();
                receivedMessage = update.getCallbackQuery().getData();

                botAnswerUtils(receivedMessage, chatId, userName);
            }
        }

        private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
            switch (receivedMessage){
                case "/start":
                    startBot(chatId, userName);
                    break;
                case "/help":
                    sendHelpText(chatId, HELP_TEXT);
                    break;
                default: break;
            }
        }

        private void startBot(long chatId, String userName) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
            message.setReplyMarkup(Buttons.Buttons.inlineMarkup());

            try {
                execute(message);
                log.info("Reply sent");
            } catch (TelegramApiException e){
                log.error(e.getMessage());
            }
        }

        private void sendHelpText(long chatId, String textToSend){
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(textToSend);

            try {
                execute(message);
                log.info("Reply sent");
            } catch (TelegramApiException e){
                log.error(e.getMessage());
            }
        }
        @Autowired
        private UserRepository userRepository;
        private void updateDB(long userId, String userName) {
            if(userRepository.findById(userId).isEmpty()){
                User user = new User();
                user.setId(userId);
                user.setName(userName);
                //сразу добавляем в столбец каунтера 1 сообщение
                user.setMsg_numb(1);

                userRepository.save(user);
                log.info("Added to DB: " + user);
            } else {
                userRepository.updateMsgNumberByUserId(userId);
            }
        }
        @Override
        public void onUpdateReceived(@NotNull Update update) {
            long chatId = 0;
            long userId = 0;
            String userName = null;
            String receivedMessage;

            if(update.hasMessage()) {
                chatId = update.getMessage().getChatId();
                userId = update.getMessage().getFrom().getId();
                userName = update.getMessage().getFrom().getFirstName();

                if (update.getMessage().hasText()) {
                    receivedMessage = update.getMessage().getText();
                    botAnswerUtils(receivedMessage, chatId, userName);
                }
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
                userId = update.getCallbackQuery().getFrom().getId();
                userName = update.getCallbackQuery().getFrom().getFirstName();
                receivedMessage = update.getCallbackQuery().getData();

                botAnswerUtils(receivedMessage, chatId, userName);
            }

            if(chatId == Long.valueOf(config.getChatId())){
                updateDB(userId, userName);
            }
        }
    }
}
