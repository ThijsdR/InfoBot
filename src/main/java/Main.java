import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.Console;

public class Main {
    public static void main(String[] args) {

        /* Initialize Api Context */
        ApiContextInitializer.init();

        /* Instantiate Telegram Bots API */
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        /* Register Inf0_B0t */
        try {
            telegramBotsApi.registerBot(new Inf0_B0t());
        } catch (TelegramApiException tea) {
            tea.printStackTrace();
        }

        System.out.println("Inf0_B0t succesfully started");
    }
}
