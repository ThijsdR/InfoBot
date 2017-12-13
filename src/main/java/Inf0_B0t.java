import botCommands.CommandBuilder;
import botCommands.clashofclans.clans.Clan;
import botCommands.ns.NSVertrektijden;
import nl.pvanassen.ns.NsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import utility.IConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Inf0_B0t extends TelegramLongPollingBot {

    private NsApi nsApi = new NsApi(IConstants.NSAPILOGIN, IConstants.NSAPIPASSWORD);

    public void onUpdateReceived(Update update) {

        /* Check if update has text */
        if (update.hasMessage() && update.getMessage().hasText()) {

            infoBotLog(update);

            /* Set variables */
            String messageText = update.getMessage().getText();
            String[] commands = messageText.split("\\s+");
            SendMessage sendMessage = new SendMessage();
            long chatID = update.getMessage().getChatId();

            CommandBuilder cmdBuilder = new CommandBuilder(messageText, commands, sendMessage, chatID);

            processCommand(cmdBuilder);
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

    private void processCommand(CommandBuilder cmdBuilder) {
        if (cmdBuilder.getCommands()[0].equals("/info")) {
            cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];
            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(Clan.getClanInfo("https://api.clashofclans.com/v1/clans?name=" + cmdBuilder.getCommands()[1]));
            runCommand(cmdBuilder.getSendMessage());
        }
        if (cmdBuilder.getCommands()[0].equals("/clandonaties")) {
            cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];
            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(Clan.getClanDonaties("https://api.clashofclans.com/v1/clans/" + cmdBuilder.getCommands()[1] + "/members?limit=50"));
            runCommand(cmdBuilder.getSendMessage());
        }
        if (cmdBuilder.getCommands()[0].equals("hallo")) {
            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Hallo daar!\nIk ben Inf0_Bot en ik ben gemaakt door David");
            runCommand(cmdBuilder.getSendMessage());
        }
        if (cmdBuilder.getCommands()[0].equals("/trein")) {
            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NSVertrektijden.getVertrektijden(nsApi, cmdBuilder.getCommands()[1]));
            runCommand(cmdBuilder.getSendMessage());
        }
        if (cmdBuilder.getMessageText().contains("homo")) {
            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Bam is de grootste homo! :)");
            runCommand(cmdBuilder.getSendMessage());
        }
    }
}
