package ru.ruslankhayrullin.mimimimetr.bot.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ruslankhayrullin.mimimimetr.bot.TelegramBot;
import ru.ruslankhayrullin.mimimimetr.model.persistence.CatRateInfo;
import ru.ruslankhayrullin.mimimimetr.persistence.service.CatRateInfoService;

import static ru.ruslankhayrullin.mimimimetr.constant.Constants.DELIMETER;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.ERROR_TEXT_INFO;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.IMAGES_FOLDER_PATH;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.IMAGE_TYPE;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.START_BUTTON_TEXT;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.START_INFO_TEXT;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.START_VOTE_TEXT;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.TOP_BUTTON_TEXT;
import static ru.ruslankhayrullin.mimimimetr.constant.Constants.TOP_INFO_TEXT;

/**
 * Handling interaction with bot
 */
@Slf4j
@Service
public class BotInteractionHandler implements UpdateHandler {


    private Map<Long, List<Pair<String, String>>> currentCatRateInfoListMap = new HashMap<>();

    private final CatRateInfoService catRateInfoService;
    private Map<Long, Pair<String, String>> currentVoteForChatId = new HashMap<>();

    public BotInteractionHandler(CatRateInfoService catRateInfoService) {
        this.catRateInfoService = catRateInfoService;
    }

    @Override
    public void handle(TelegramBot bot, Update update) {

        var chatId = update.getMessage().getChatId();
        var voteCatName = update.getMessage().getText();

        if((currentCatRateInfoListMap.isEmpty() || !currentCatRateInfoListMap.containsKey(chatId)) && !voteCatName.equals(START_BUTTON_TEXT)) {
            showStartButton(chatId, bot);
            return;
        }

        if(!currentCatRateInfoListMap.containsKey(chatId)) {
            List<Pair<String, String>> pairForVote = new ArrayList<>();
            var randomCats = catRateInfoService.getRandomAllCats();
            randomCats.forEach(cat1 -> randomCats.forEach(cat2 -> {
                if(!cat1.getNameCat().equals(cat2.getNameCat())
                    && pairForVote.stream().noneMatch(s -> s.getFirst().equals(cat2.getNameCat()) && s.getSecond().equals(cat1.getNameCat())))  {
                    pairForVote.add(Pair.of(cat1.getNameCat(), cat2.getNameCat()));
                }
            }));
            currentCatRateInfoListMap.put(chatId, pairForVote);
            sendSimpleText(chatId, bot, START_VOTE_TEXT);
        }

        log.info(currentCatRateInfoListMap.get(chatId).toString());


        var currentCatRateInfoList = currentCatRateInfoListMap.get(update.getMessage().getChatId());

        if(currentCatRateInfoList.isEmpty() && voteCatName.equals(TOP_BUTTON_TEXT)) {
            showResults(bot, chatId);
            return;
        }

        if(currentCatRateInfoList.isEmpty()) {
            showTopButton(chatId, bot);
            return;
        }

        if(!currentVoteForChatId.containsKey(chatId)) {
            vote(bot, chatId);
        } else if (!currentVoteForChatId.get(chatId).getFirst().equals(voteCatName)
                && !currentVoteForChatId.get(chatId).getSecond().equals(voteCatName)) {
            sendErrorMessage(chatId, bot);
        } else {
            vote(bot, chatId);
            catRateInfoService.incrementCatPoints(voteCatName);
        }
    }

    private void sendSimpleText(Long chatId, TelegramBot bot, String text) {
        log.info("sendSimpleText.enter; text = {}", text);
        var replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        replyMessage.setText(text);
        try {
            bot.execute(replyMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("sendSimpleText.exit;");
    }

    private void showTopButton(Long chatId, TelegramBot bot) {
        log.info("showTopButton.enter;");
        var replyMessage = createReplyMessage(List.of(TOP_BUTTON_TEXT), chatId);
        replyMessage.setText(TOP_INFO_TEXT);
        try {
            bot.execute(replyMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("showTopButton.exit;");
    }

    private void showStartButton(Long chatId, TelegramBot bot) {
        log.info("showStartButton.enter;");

        var replyMessage = createReplyMessage(List.of(START_BUTTON_TEXT), chatId);
        replyMessage.setText(START_INFO_TEXT);
        try {
            bot.execute(replyMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("showStartButton.exit;");
    }

    private void sendErrorMessage(Long chatId, TelegramBot bot) {
        log.info("sendErrorMessage.enter;");
        var replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        replyMessage.setText(ERROR_TEXT_INFO);
        try {
            bot.execute(replyMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("sendErrorMessage.exit;");
    }

    private void vote(TelegramBot bot, Long chatId) {
        log.info("vote.enter;");
        var currentPair = currentCatRateInfoListMap.get(chatId).stream().findFirst().get();
        currentVoteForChatId.put(chatId, Pair.of(currentPair.getFirst(), currentPair.getSecond()));
        currentCatRateInfoListMap.get(chatId).remove(0);
        var replyMessageFirst = new SendMessage();
        replyMessageFirst.setChatId(chatId);
        replyMessageFirst.setText(currentPair.getFirst());
        var replyMessageSecond = createReplyMessage(List.of(currentPair.getFirst(), currentPair.getSecond()), chatId);
        replyMessageSecond.setChatId(chatId);
        replyMessageSecond.setText(currentPair.getSecond());
        try {
            bot.execute(replyMessageFirst);
            bot.execute(createSendPhotoReply(currentPair.getFirst(), chatId));
            bot.execute(replyMessageSecond);
            bot.execute(createSendPhotoReply(currentPair.getSecond(), chatId));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("vote.exit;");
    }

    private SendMessage createReplyMessage(List<String> buttons, Long chatId) {
        log.info("createReplyMessage.enter;");
        var replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRows.add(keyboardRow);
        buttons.forEach(keyboardRow::add);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyMessage.setReplyMarkup(replyKeyboardMarkup);
        return replyMessage;
    }

    private SendPhoto createSendPhotoReply(String catName, Long chatId) {
        log.info("createSendPhotoReply.enter;");
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(new File(IMAGES_FOLDER_PATH + DELIMETER + catName + IMAGE_TYPE)))
                .parseMode(ParseMode.HTML)
                .build();
    }

    private void showResults(TelegramBot bot, Long chatId) {
        log.info("showResults.enter;");
        List<CatRateInfo> catRateInfosResult = catRateInfoService.getAllCatsOrderByPoints();
        catRateInfosResult.forEach(s -> {
            var replyMessage = new SendMessage();
            replyMessage.setChatId(chatId);
            replyMessage.setText(s.getNameCat());
            try {
                bot.execute(replyMessage);
                bot.execute(createSendPhotoReply(s.getNameCat(), chatId));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("showResults.exit;");
    }
}
