package ru.ruslankhayrullin.mimimimetr.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ruslankhayrullin.mimimimetr.bot.TelegramBot;

/**
 * Delegate for handling updates from Telegram API
 */
public interface UpdateHandler {
    void handle(TelegramBot bot, Update update);
}
