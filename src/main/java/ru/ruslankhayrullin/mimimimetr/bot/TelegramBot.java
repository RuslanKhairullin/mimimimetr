package ru.ruslankhayrullin.mimimimetr.bot;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.ruslankhayrullin.mimimimetr.bot.handler.UpdateHandler;
import ru.ruslankhayrullin.mimimimetr.model.persistence.CatRateInfo;
import ru.ruslankhayrullin.mimimimetr.persistence.service.CatRateInfoService;

import static ru.ruslankhayrullin.mimimimetr.constant.Constants.ZERO_POINTS;


/**
 * Sends UpdateInfo to telegram chat(s) where bot included
 */
@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final Map<String, UpdateHandler> updateHandlers;

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.chats.deleteAfterErrors}")
    private Integer deleteAfterErrors;
    @Value("${telegram.bot.chats.resendAfterDelay}")
    private Integer resendAfterDelay;

    private CatRateInfoService catRateInfoService;

    @Autowired
    public TelegramBot(Map<String, UpdateHandler> updateHandlers, CatRateInfoService catRateInfoService) {
        this.updateHandlers = updateHandlers;
        this.catRateInfoService = catRateInfoService;
    }

    @PostConstruct
    private void initialize() {
        try {
            var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            loadImages();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived.enter;");
        if (update.hasMessage() && Objects.nonNull(update.getMessage().getText())) {
            log.info(update.getMessage().getText());
            updateHandlers.get("botInteractionHandler").handle(this, update);
        }
        log.info("onUpdateReceived.enter;");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    void loadImages() {
        log.info("loadImages.enter;");
        File folders = new File(getClass().getClassLoader().getResource("images").getFile());
        for (File file : Objects.requireNonNull(folders.listFiles())) {
            catRateInfoService.saveChat(CatRateInfo.builder()
                    .nameCat(file.getName().replace(".jpg", ""))
                    .points(ZERO_POINTS)
                    .build());
        }
        log.info("loadImages.exit;");

    }
}
