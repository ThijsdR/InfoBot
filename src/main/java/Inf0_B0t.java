import utility.CommandContainer;
import botCommands.clashofclans.clans.CoC_Clan;
import botCommands.clashofclans.clans.CoC_ClanTagMembers;
import botCommands.clashroyale.CR_Clan;
import botCommands.ns.NSStoringenWerkzaamheden;
import botCommands.ns.NSVertrektijden;
import botCommands.weather.CurrentWeather;
import botCommands.weather.WeatherForecast;
import com.vdurmont.emoji.EmojiParser;
import nl.pvanassen.ns.NsApi;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import utility.Commands;
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
            String[] treinCommands = messageText.split("\\s+", 2);
            SendMessage sendMessage = new SendMessage();
            long chatID = update.getMessage().getChatId();

            processCommand(new CommandContainer(messageText, commands, treinCommands, sendMessage, chatID));
        }
    }

    /**
     * @return  bot username
     */
    public String getBotUsername() {
        return "ClashInfo_Bot";
    }

    /**
     * @return  bot token
     */
    public String getBotToken() {
        return "455982433:AAG0Y0tOtvK6QrGaetlqdZh_t9lDW5ks9cc";
    }

    /**
     * Log de binnengekomen tekst
     *
     * @param update   Binnengekomen tekst
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

    private void runCommandMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException tea) {
            tea.printStackTrace();
        }
    }

    private void runCommandDocument(SendDocument sendDocument) {
        try {
            sendDocument(sendDocument);
        } catch (TelegramApiException tea) {
            tea.printStackTrace();
        }
    }

    private void processCommand(CommandContainer cmdBuilder) {

        COMMAND_CONTROL :
        {
            /* Controleer of de tekst een commando is */
            if (cmdBuilder.getCommands()[0].startsWith("/")) {

                /* Clash of Clans commando's */
                if (cmdBuilder.getCommands()[0].equals(Commands.COCCLANINFO.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Clan.getClanInfo(Commands.COCCLANINFO.getEditableURL() + cmdBuilder.getCommands()[1]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Clan.getClanInfo(Commands.COCCLANINFO.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.COCCLANDONATIONS.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Clan.getClanDonaties(Commands.COCCLANDONATIONS.getEditableURL() + cmdBuilder.getCommands()[1] + "/members"));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Clan.getClanDonaties(Commands.COCCLANDONATIONS.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.COCCLANMEMBER.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Clan.getClanMembers(Commands.COCCLANMEMBER.getEditableURL() + cmdBuilder.getCommands()[1] + "/members"));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Clan.getClanMembers(Commands.COCCLANMEMBER.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.COCCLANMEMBERINFO.getCommand())) {
                    if (cmdBuilder.getCommands().length > 2 && cmdBuilder.getCommands()[1].startsWith("#")) {
                        String clanTag = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_ClanTagMembers.getClanMemberInfo(Commands.COCCLANMEMBERINFO.getEditableURL() + clanTag + "/members", cmdBuilder.getCommands()[2]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands().length > 1 && !cmdBuilder.getCommands()[1].contains("#")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_ClanTagMembers.getClanMemberInfo(Commands.COCCLANMEMBERINFO.getDefaultURL(), cmdBuilder.getCommands()[1]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik na het commando nog 1 of 2 parameters nodig.\nBijvoorbeeld: /cocmemberinfo #joc9cpy BlackWing\nof: /cocmemberinfo BlackWing (standaard brabant2.0)");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                // ToDo: Check inbouwen!!
                if (cmdBuilder.getCommands()[0].equals(Commands.COCCLANMEMBERSTOFILE.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];

                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(CoC_Clan.getClanMembersFile(Commands.COCCLANMEMBERSTOFILE.getEditableURL() + cmdBuilder.getCommands()[1] + "/members"));
                        sendDocumentrequest.setCaption("Clanleden overzicht");
                        runCommandDocument(sendDocumentrequest);
                    } else {
                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(CoC_Clan.getClanMembersFile(Commands.COCCLANMEMBERSTOFILE.getDefaultURL()));
                        sendDocumentrequest.setCaption("Clanleden overzicht");
                        runCommandDocument(sendDocumentrequest);
                    } break COMMAND_CONTROL;
                }

                /* Clash Royale commando's */
                if (cmdBuilder.getCommands()[0].equals(Commands.CRCLANINFO.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanInfo(Commands.CRCLANINFO.getEditableURL() + cmdBuilder.getCommands()[1].substring(1)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanInfo(Commands.CRCLANINFO.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.CRCLANROLES.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanRoles(Commands.CRCLANROLES.getEditableURL() + cmdBuilder.getCommands()[1].substring(1)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanRoles(Commands.CRCLANROLES.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.CRCLANDONATIONS.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanDonations(Commands.CRCLANDONATIONS.getEditableURL() + cmdBuilder.getCommands()[1].substring(1)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanDonations(Commands.CRCLANDONATIONS.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.CRCLANCHEST.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanchestContribution(Commands.CRCLANCHEST.getEditableURL() + cmdBuilder.getCommands()[1].substring(1)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanchestContribution(Commands.CRCLANCHEST.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }

                /* NS commando's */
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.TREINTIJDEN.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NSVertrektijden.getVertrektijden(nsApi, cmdBuilder.getLocatieCommands()[1]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een treinstation in Nederland nodig...\n\nBijvoorbeeld: /treintijden Rotterdam Centraal");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.TREINSTORINGEN.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NSStoringenWerkzaamheden.getStoringen(nsApi));
                    runCommandMessage(cmdBuilder.getSendMessage()); break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.TREINWERKZAAMHEDEN.getCommand())) {
                    SendDocument sendDocumentrequest = new SendDocument();
                    sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                    sendDocumentrequest.setNewDocument(NSStoringenWerkzaamheden.getWerkzaamheden(nsApi));
                    sendDocumentrequest.setCaption("Actuele en aankomende werkzaamheden");
                    runCommandDocument(sendDocumentrequest); break COMMAND_CONTROL;
                }

                /* Weer commando's */
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.WEERHUIDIG.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CurrentWeather.getCurrentWeather("http://api.wunderground.com/api/" + IConstants.WUNDERGROUNDAPIKEY + "/conditions/q/nl/" + cmdBuilder.getLocatieCommands()[1].replace(" ", "_") + ".json"));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een plaats in Nederland nodig...\n\nBijvoorbeeld: /weerhuidig Den Haag");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.WEERVOORSPELLING.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(WeatherForecast.getForecast("http://api.wunderground.com/api/" + IConstants.WUNDERGROUNDAPIKEY + "/forecast/q/nl/" + cmdBuilder.getLocatieCommands()[1].replace(" ", "_") + ".json"));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een plaats in Nederland nodig...\n\nBijvoorbeeld: /weervoorspelling Den Haag");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }

                /* Overige commando's */
                if (cmdBuilder.getCommands()[0].equals(Commands.HELP.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Een overzicht met alle commando's en uitleg volgt nog....");
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.JOKE.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(IConstants.JOKE);
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.HALLO.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Hallo!\nIk ben Inf0_B0t");
                    runCommandMessage(cmdBuilder.getSendMessage());
                } else {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(EmojiParser.parseToUnicode(":exclamation:ERROR: Ik ken dit commando helaas niet.\nStuur /help voor beschikbare commando's"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                }
            }
        }

        if (cmdBuilder.getMessageText().toLowerCase().contains("homo".toLowerCase())) {
            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Bam is de grootste homo! :)");
            runCommandMessage(cmdBuilder.getSendMessage());
        }
    }
}
