package all.telegramBOT;

import all.master.Coordinator;
import controller.Controller;
import main.java.system.Printer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TelegramBotClass extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            long chat_id = update.getMessage().getChatId();
            Coordinator.getInstance().updateChatIdList(chat_id);
            Printer.getInstance().print("%%%%%%%%%%%%%%%%%%%%%%%%%%% chat id : " + chat_id + " %%%%%%%%%%%%%%%%%", "red");
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText("message registered, now you will receive info about malfunctions");
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "MalfunctionSDCCBot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "570502022:AAEjGx-4lcN-4PxYMbA3NPTwmF1KvfIjgTk";
    }

    public void sendMessage(String m, long chat_id){
        Printer.getInstance().print("%%%%%%%%%invio il messaggio%%%%%%%%%%%%%%\n%%%%%%%%%%%%%%\n%%%%%%%%%%%%%%\n", "yellow");
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(m);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

