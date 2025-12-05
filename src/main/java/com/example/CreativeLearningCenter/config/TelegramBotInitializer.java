package com.example.CreativeLearningCenter.config;

import com.example.CreativeLearningCenter.service.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class TelegramBotInitializer {

    private final TelegramBotService telegramBotService;

    public TelegramBotInitializer(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBotService);
            log.info("✅ Telegram bot started successfully!");
        } catch (TelegramApiException e) {
            log.error("❌ Failed to start Telegram bot: {}", e.getMessage(), e);
        }
    }
}