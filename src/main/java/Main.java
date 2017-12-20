import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import utility.ReportGenerator;

import java.util.Calendar;
import java.util.Timer;

public class Main {
    public static void main(String[] args) {

        /* Maak elke dag, om 03:00 uur, een rapport van de data van CoC clanleden */
        Timer timer = new Timer();
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR, 3);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        timer.schedule(new ReportGenerator(), date.getTime(), 1000 * 60 * 60 * 24);

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
