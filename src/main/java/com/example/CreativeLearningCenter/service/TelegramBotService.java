package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.TelegramLinkByCodeRequest;
import com.example.CreativeLearningCenter.dto.TelegramLinkByPhoneRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    private final String botUsername;
    private final TelegramLinkingService linkingService;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            @Lazy TelegramLinkingService linkingService) {  // Add @Lazy here
        super(botToken);
        this.botUsername = botUsername;
        this.linkingService = linkingService;
        log.info("TelegramBotService constructor called");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        log.info("Message from {}: {}", chatId, text);

        if (text.equals("/start")) {
            sendMessage(chatId,
                    "👋 Welcome to Creative Learning Center!\n\n" +
                            "To link your account, send:\n" +
                            "• Phone: +998XXXXXXXXX\n" +
                            "• Code: STU-XXXXX");
            return;
        }

        if (text.startsWith("+998") && text.length() == 13) {
            linkByPhone(chatId, text);
            return;
        }

        if (text.matches("^STU-\\d{5}$")) {
            linkByCode(chatId, text);
            return;
        }

        sendMessage(chatId, "❌ Invalid format!");
    }

    private void linkByPhone(Long chatId, String phoneNumber) {
        try {
            TelegramLinkByPhoneRequest request = new TelegramLinkByPhoneRequest();
            request.setPhoneNumber(phoneNumber);
            request.setChatId(chatId);

            linkingService.linkByPhone(request);
            sendMessage(chatId, "✅ Linked successfully!");
        } catch (Exception e) {
            log.error("Link error: {}", e.getMessage());
            sendMessage(chatId, "❌ Not found");
        }
    }

    private void linkByCode(Long chatId, String code) {
        try {
            TelegramLinkByCodeRequest request = new TelegramLinkByCodeRequest();
            request.setCode(code);
            request.setChatId(chatId);

            linkingService.linkByCode(request);
            sendMessage(chatId, "✅ Linked successfully!");
        } catch (Exception e) {
            log.error("Link error: {}", e.getMessage());
            sendMessage(chatId, "❌ Not found");
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Send failed: {}", e.getMessage());
        }
    }
}