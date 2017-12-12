import botCommands.clans.Clan;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Inf0_B0t extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {

        /* Check if update has text */
        if (update.hasMessage() && update.getMessage().hasText()) {

            infoBotLog(update);

            /* Set variables */
            String messageText = update.getMessage().getText();
            String[] commands = messageText.split("\\s+");
            long chatID = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage();

            if (commands[0].equals("/info")) {
                commands[1] = commands[1].startsWith("#") ? "%23" + commands[1].substring(1) : commands[1];
                sendMessage.setChatId(chatID).setText(Clan.getClanInfo("https://api.clashofclans.com/v1/clans?name=" + commands[1]));
                runCommand(sendMessage);
            }
            if (commands[0].equals("/clandonaties")) {
                commands[1] = commands[1].startsWith("#") ? "%23" + commands[1].substring(1) : commands[1];
                sendMessage.setChatId(chatID).setText(Clan.getClanDonaties("https://api.clashofclans.com/v1/clans/" + commands[1] + "/members?limit=50"));
                runCommand(sendMessage);
            }
            if (messageText.contains("homo")) {
                sendMessage.setChatId(chatID).setText("Bam is de grootste homo! :)");
                runCommand(sendMessage);
            }
        }
    }

    /**
     *
     * @return  bot username
     */
    public String getBotUsername() {
        return "ClashInfo_Bot";
    }

    /**
     *
     * @return  bot token
     */
    public String getBotToken() {
        return "455982433:AAG0Y0tOtvK6QrGaetlqdZh_t9lDW5ks9cc";
    }

    /**
     *
     * @param update
     */
    private void infoBotLog(Update update) {
        String userFirstName = update.getMessage().getChat().getFirstName();
        String userLastName = update.getMessage().getChat().getLastName();
        String userUserName = update.getMessage().getChat().getUserName();
        long userID = update.getMessage().getChat().getId();
        String messageText = update.getMessage().getText();
        long chatID = update.getMessage().getChatId();

        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from: " + userFirstName + " " + userLastName +
                "\nUsername: " + userUserName +
                "\nUser ID = " + Long.toString(userID) +
                "\nChat ID = " + Long.toString(chatID) +
                "\n\n Text - " + messageText);
    }

    private void runCommand(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException tea) {
            tea.printStackTrace();
        }
    }
}
