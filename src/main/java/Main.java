import help.H_Help;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main
{
    public static void main(String[] args)
    {

        /* Initialize Api Context */
        ApiContextInitializer.init();

        /* Instantiate Telegram Bots API */
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        /* Register Inf0_B0t */
        try
        {
            telegramBotsApi.registerBot(new Inf0_B0t());
        } catch (TelegramApiException tea)
        {
            System.out.println(H_Help.exceptionStacktraceToString(tea));
        }

        System.out.println("Inf0_B0t succesfully started");
    }
}
