import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
            long chatID = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage();

            if (messageText.equals("/info")) {
                sendMessage.setChatId(chatID).setText(getClanInfo("https://api.clashofclans.com/v1/clans?name=brabant2.0"));
            }
            if (messageText.equals("homo")) {
                sendMessage.setChatId(chatID).setText("Bam is de grootste homo! :)");
            }

            try {
                execute(sendMessage);
            } catch (TelegramApiException tea) {
                tea.printStackTrace();
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

    private String getClanInfo(String urlString) {

        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjFjZmVlZjA1LThhZjUtNGI5Mi04NjFhLWIzYzUxNDVmYzI3OCIsImlhdCI6MTUxMzA3NDQ3OSwic3ViIjoiZGV2ZWxvcGVyLzY3ZDQxYzE1LWIzN2EtMGMzNy0yMGViLTQ3Y2JjOTQzNWE3ZSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjk1Ljk3LjExNS4xMjYiXSwidHlwZSI6ImNsaWVudCJ9XX0.UTZ8CAvHwPoYvqbp3tOHl0n006Ph1LesPNmpBHmKLQZI80uU8aHefqk_BtY7xTjDwClZlAfnzradprqwFQgLzA");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String data = String.valueOf(content);

        JSONObject json = new JSONObject(data);
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("Clan: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Clan tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Clan type: ").append(jsonArray.getJSONObject(i).getString("type")).append("\n");
            botResponse.append("Members: ").append(jsonArray.getJSONObject(i).getInt("members")).append("\n");
        }

        return botResponse.toString();
    }
}
