package config;

import lombok.Data;
import lombok.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

public class BotConfig {

    @Configuration
    @Data
    @PropertySource("config.properties")
    public class BotConfig {
        @Value("${bot.name}") String botName;
        @Value("${bot.token}") String token;
        @Value("${bot.chatId}") String chatId;
    }
}
