import clashofclans.*;
import help.H_Help;
import utility.CommandContainer;
import clashroyale.CR_Clan;
import ns.NS_StoringenWerkzaamheden;
import ns.NS_Vertrektijden;
import weather.W_Current;
import weather.W_Forecast;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Het brein van Inf0_B0t.
 * Hierin worden alle nodige handelingen verricht en commando's gecontroleerd.
 */
public class Inf0_B0t extends TelegramLongPollingBot {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(Inf0_B0t.class.getName());

    /* Velden */
    private NsApi nsApi = new NsApi(IConstants.NSAPILOGIN, IConstants.NSAPIPASSWORD);
    private CoC_ServerState serverStatusCoC;
    private boolean beledigingen = true;
    private final long brabantTelegramChatID = -151298765;

    /* Constructor */
    public Inf0_B0t() {

        /* Set up LOGGER */
        LOGGER.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);

        /* Maak elke dag, om 03:00 uur, een rapport van de data van CoC clanleden */
        Runnable reportGenerator = () -> CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), true);

        /* Bepaal de tijdzone en de tijd wanneer het rapport gemaakt moet worden */
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Europe/Amsterdam");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext3;
        zonedNext3 = zonedNow.withHour(3).withMinute(0).withSecond(0);

        if (zonedNow.compareTo(zonedNext3) > 0) {
            zonedNext3 = zonedNext3.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext3);
        long initalDelay = duration.getSeconds();

        /* Voer de taak elke dag 1 keer uit */
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(reportGenerator, initalDelay, 24 * 60 * 60, TimeUnit.SECONDS);

        /* Check elke minuut de serverstatus van Clash of Clans */
        Runnable serverChecker = () -> {

            /* Controleer de status */
            serverStatusCoC = CoC_PROC.getServerStatusCoC();

            switch (serverStatusCoC) {
                case COCWENTOFFLINE:    // Server is offline gegaan
                    LOGGER.log(Level.WARNING, "Server went OFFLINE");
                    runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(CoC_ServerState.COCWENTOFFLINE.getStateDescription()));
                    break;
                case COCWENTONLINE:     // Server is online gegaan
                    LOGGER.log(Level.WARNING, "Server went ONLINE");
                    runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(CoC_ServerState.COCWENTONLINE.getStateDescription()));
                    break;
                case COCOFFLINE:        // Server is offline
                    LOGGER.log(Level.FINE, "Server is OFFLINE");
                    break;
                case COCONLINE:         // Server is online
                    LOGGER.log(Level.FINE, "Server is ONLINE");
                    break;
                default:
            }
        };

        /* Service om de servercheck uit te voeren en voer deze om de minuut uit*/
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(serverChecker, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Ontvang het bericht en verwerk dit
     *
     * @param update    Ontvangen bericht
     */
    public void onUpdateReceived(Update update) {

        /* Check if update has text */
        if (update.hasMessage() && update.getMessage().hasText()) {

            /* Log de ontvangen update */
            infoBotLog(update);

            /* Set variables */
            String messageText = update.getMessage().getText();
            String[] commands = messageText.split("\\s+");
            String[] treinCommands = messageText.split("\\s+", 2);
            SendMessage sendMessage = new SendMessage();
            long chatID = update.getMessage().getChatId();

            /* Verwerk het opgegeven commando */
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
     * Log het binnengekomen bericht
     *
     * @param update   Ontvangen bericht
     */
    private void infoBotLog(Update update) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Object[] log = new Object[]{dateFormat.format(new Date()),
                update.getMessage().getChat().getFirstName(),
                update.getMessage().getChat().getLastName(),
                update.getMessage().getChat().getUserName(),
                update.getMessage().getChat().getId(),
                update.getMessage().getChatId(),
                update.getMessage().isGroupMessage(),
                update.getMessage().getText()
        };

        LOGGER.log(Level.FINE, "[FROM: {1} {2}] [USERNAME: {3}] [USER_ID: {4}] [CHAT_ID: {5}] [GROUP MESSAGE: {6}] [MESSAGE TEXT: {7}]", log);
    }

    /**
     * Stuur aan de hand van een SendMessage een bericht terug naar een gebruiker/groep
     *
     * @param sendMessage   Bericht wat terug gestuurd moet worden
     */
    private void runCommandMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException tea) {
            LOGGER.log(Level.SEVERE, tea.toString(), tea);
        }
    }

    /**
     * Stuur aan de hand van een SendDocument een document terug naar een gebruiker/groep
     *
     * @param sendDocument  Document wat terug gestuurd moet worden
     */
    private void runCommandDocument(SendDocument sendDocument) {
        try {
            sendDocument(sendDocument);
        } catch (TelegramApiException tea) {
            LOGGER.log(Level.SEVERE, tea.toString(), tea);
        }
    }

    /**
     * Voer op basis van de invoer het bijpassende commando uit
     *
     * @param cmdBuilder    Bevat alle gegevens om een reactie te sturen
     */
    private void processCommand(CommandContainer cmdBuilder) {

        COMMAND_CONTROL :
        {
            /* Controleer of de tekst een commando is */
            if (cmdBuilder.getCommands()[0].startsWith("/")) {

                ///////////////////////////////////////////////////
                //////        Clash of Clans commando's      //////
                ///////////////////////////////////////////////////
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
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_ClanMember.getClanMemberInfo(Commands.COCCLANMEMBERINFO.getEditableURL() + clanTag + "/members", cmdBuilder.getCommands()[2]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands().length > 1 && !cmdBuilder.getCommands()[1].contains("#")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_ClanMember.getClanMemberInfo(Commands.COCCLANMEMBERINFO.getDefaultURL(), cmdBuilder.getCommands()[1]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik na het commando nog 1 of 2 parameters nodig.\nBijvoorbeeld: /cocmemberinfo #joc9cpy BlackWing\nof: /cocmemberinfo BlackWing (standaard brabant2.0)");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.COCCLANMEMBERSTOFILE.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1 && cmdBuilder.getCommands()[1].startsWith("#")) {
                        cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];

                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getEditableURL() + cmdBuilder.getCommands()[1] + "/members", false));
                        sendDocumentrequest.setCaption("Clanleden overzicht");
                        runCommandDocument(sendDocumentrequest); break COMMAND_CONTROL;
                    } if (cmdBuilder.getCommands().length > 1 && !cmdBuilder.getCommands()[1].startsWith("#")){
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik na het commando nog een parameter nodig.\nBijvoorbeeld: /cocclanmembersfile #joc9cpy");
                        runCommandMessage(cmdBuilder.getSendMessage()); break COMMAND_CONTROL;
                    } else {
                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), false));
                        sendDocumentrequest.setCaption("Clanleden overzicht");
                        runCommandDocument(sendDocumentrequest);
                    } break COMMAND_CONTROL;
                }
                //////     Einde Clash of Clans commando's   //////

                ///////////////////////////////////////////////////
                //////          Clash Royale commando's      //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getCommands()[0].equals(Commands.CRCLANINFO.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanInfo(Commands.CRCLANINFO.getEditableURL() + cmdBuilder.getCommands()[1].substring(1)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanInfo(Commands.CRCLANINFO.getDefaultURL()));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.CRCLANMEMBERS.getCommand())) {
                    if (cmdBuilder.getCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanMembers(Commands.CRCLANMEMBERS.getEditableURL() + cmdBuilder.getCommands()[1].substring(1)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CR_Clan.getClanMembers(Commands.CRCLANMEMBERS.getDefaultURL()));
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
                //////     Einde Clash Royale commando's     //////

                ///////////////////////////////////////////////////
                //////          NS/trein commando's          //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.TREINTIJDEN.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NS_Vertrektijden.getVertrektijden(nsApi, cmdBuilder.getLocatieCommands()[1]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een treinstation in Nederland nodig...\n\nBijvoorbeeld: /treintijden Rotterdam Centraal");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.TREINSTORINGEN.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NS_StoringenWerkzaamheden.getStoringen(nsApi));
                    runCommandMessage(cmdBuilder.getSendMessage()); break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.TREINWERKZAAMHEDEN.getCommand())) {
                    SendDocument sendDocumentrequest = new SendDocument();
                    sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                    sendDocumentrequest.setNewDocument(NS_StoringenWerkzaamheden.getWerkzaamheden(nsApi));
                    sendDocumentrequest.setCaption("Actuele en aankomende werkzaamheden");
                    runCommandDocument(sendDocumentrequest); break COMMAND_CONTROL;
                }
                //////         Einde NS/trein commando's     //////

                ///////////////////////////////////////////////////
                //////      Weergerelateerde commando's      //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.WEERHUIDIG.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(W_Current.getCurrentWeather("http://api.wunderground.com/api/" + IConstants.WUNDERGROUNDAPIKEY + "/conditions/q/nl/" + cmdBuilder.getLocatieCommands()[1].replace(" ", "_") + ".json"));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een plaats in Nederland nodig...\n\nBijvoorbeeld: /weerhuidig Den Haag");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].equals(Commands.WEERVOORSPELLING.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(W_Forecast.getForecast("http://api.wunderground.com/api/" + IConstants.WUNDERGROUNDAPIKEY + "/forecast/q/nl/" + cmdBuilder.getLocatieCommands()[1].replace(" ", "_") + ".json"));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een plaats in Nederland nodig...\n\nBijvoorbeeld: /weervoorspelling Den Haag");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                //////    Einde weergerelateerde commando's  //////

                ///////////////////////////////////////////////////
                //////          Overige commando's           //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getCommands()[0].equals(Commands.HELP.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(H_Help.getHelp());
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.JOKE.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(IConstants.JOKE);
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.HALLO.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Hallo!\nIk ben Inf0_B0t\n\nVersie: " + IConstants.VERSION + "\n\nStatus: " + (beledigingen ? "Eerlijk" : "Empatisch"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].equals(Commands.MODE.getCommand())) {
                    beledigingen = !beledigingen;
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("-- MODE SWITCH --\n" + (beledigingen ? "Empatisch -> Eerlijk" : "Eerlijk -> Empatisch"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                } else {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(EmojiParser.parseToUnicode(":exclamation:ERROR: Ik ken dit commando helaas niet.\nStuur /help voor beschikbare commando's"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                }
                //////          Einde overige commando's     //////
            }
        }

        if (beledigingen) {
            if (cmdBuilder.getMessageText().toLowerCase().contains("homo".toLowerCase()) || cmdBuilder.getMessageText().toLowerCase().contains("gay".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Bam is de grootste homo!");
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("slet".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Ed is een slet!");
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("hoer".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Hoer?... Volgens mij heb je Bas verkeerd gespeeld");
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("klaas".toLowerCase()) || cmdBuilder.getMessageText().toLowerCase().contains("duitser".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID())
                        .setText(EmojiParser.parseToUnicode(":notes::notes:\n" +
                                "Want Klaas is anders geaard\n" +
                                "Klaas is anders geaard\n" +
                                "Lekker zichzelf, dat is zeker wat waard\n" +
                                "Maar Klaas is anders geaard\n" +
                                ":musical_note::musical_note:"));
                runCommandMessage(cmdBuilder.getSendMessage());
            }
        }
    }
}
