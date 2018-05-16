package all.telegramBOT;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

public class TelegramBotStarter {

    private TelegramBotClass telegramBotClass;

    public TelegramBotStarter(){
//
        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            telegramBotClass = new TelegramBotClass();
            botsApi.registerBot(new TelegramBotClass());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, long l){
        this.telegramBotClass.sendMessage(message, l);
    }
}

