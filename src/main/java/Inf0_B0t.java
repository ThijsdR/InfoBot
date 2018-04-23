import clashofclans.*;
import com.vdurmont.emoji.EmojiParser;
import help.H_Help;
import nl.pvanassen.ns.NsApi;
import ns.NS_StoringenWerkzaamheden;
import ns.NS_Vertrektijden;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import roda.R_Boete;
import utility.CommandContainer;
import utility.Commands;
import utility.TextFormatting;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Het brein van Inf0_B0t.
 * Hierin worden alle nodige handelingen verricht en commando's gecontroleerd.
 */
public class Inf0_B0t extends TelegramLongPollingBot {

    /* Velden */
    private NsApi nsApi;
    private CoC_ServerState serverStatusCoC;
    private boolean beledigingen = true;
    private final long brabantTelegramChatID = -151298765;
    private ArrayList<CoC_PlayerContainer> cocPlayersList = new ArrayList<>();
    private String warState;
    private ArrayList<CoC_WarAttackContainer> clanWarAttacks;
    private ArrayList<CoC_WarAttackContainer> opponentWarAttacks;
    private String cocApiKey;

    /* Constructor */
    Inf0_B0t() {

        try {
            cocApiKey = FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/cocapikey.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Bepaal waar de output moet worden opgeslagen */
        try {
            PrintStream out = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                out = new PrintStream(new FileOutputStream("out/output/output.txt"));
            } else if (SystemUtils.IS_OS_LINUX) {
                out = new PrintStream(new FileOutputStream("/home/thijs/Infobotfiles/output.txt"));
            }
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /* Maak verbinding met de sql server en zet de ns api op */
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb","root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Api_user, Api_key FROM credentials WHERE Api = 'ns'");
            String nsApiLogin = null;
            String nsApiKey = null;

            while (rs.next()) {
                nsApiLogin = rs.getString("Api_user");
                nsApiKey = rs.getString("Api_key");
            }

            assert nsApiLogin != null;
            assert nsApiKey != null;
            nsApi = new NsApi(nsApiLogin, nsApiKey);
            rs.close();
            stmt.close();
            con.close();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }

        /* Huidige status van de Clash of Clans server */
        serverStatusCoC = CoC_PROC.getServerStatusCoC(cocApiKey);

        if (serverStatusCoC == CoC_ServerState.COCONLINE) {

            /* Huidige oorlogsdata van Clash of Clans */
            String currentWarData = CoC_PROC.retrieveDataSupercellAPI(Commands.COCWAROPPONENT.getDefaultURL(), cocApiKey);

            /* Huidige ledenlijst CLash of Clans */
            cocPlayersList = CoC_Clan.getCoCPlayerList(cocApiKey);

            /* Huidige oorlogsstatus in Clash of Clans */
            warState = CoC_War.getCurrentWarState(currentWarData);

            /* Als de clan bezig is met een oorlog haal dan de huidige aanvallen op */
            clanWarAttacks = CoC_War.getCurrentClanAttacks(currentWarData);
            opponentWarAttacks = CoC_War.getCurrentOpponentAttacks(currentWarData);
        }

        /* Maak elke dag, om 03:00 uur, een rapport van de data van CoC clanleden */
        Runnable reportGenerator = () -> CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), true, cocApiKey);

        /* Check elke minuut de serverstatus van Clash of Clans */
        Runnable serverChecker = () -> {

            /* Controleer de status */
            serverStatusCoC = CoC_PROC.getServerStatusCoC(cocApiKey);

            System.out.println("=========" + new Timestamp(System.currentTimeMillis()) + "=========");

            switch (serverStatusCoC) {
                case COCWENTOFFLINE:    // Server is offline gegaan
                    System.out.println("Server went OFFLINE");
                    runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(CoC_ServerState.COCWENTOFFLINE.getStateDescription()));
                    break;
                case COCWENTONLINE:     // Server is online gegaan
                    System.out.println("Server went ONLINE");
                    runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(CoC_ServerState.COCWENTONLINE.getStateDescription()));

                    /* Huidige ledenlijst CLash of Clans */
                    cocPlayersList = CoC_Clan.getCoCPlayerList(cocApiKey);

                    /* Huidige oorlogsstatus in Clash of Clans */
                    warState = CoC_War.getCurrentWarState(CoC_PROC.retrieveDataSupercellAPI(Commands.COCWAROPPONENT.getDefaultURL(), cocApiKey));

                    /* Als de clan bezig is met een oorlog haal dan de huidige aanvallen op */
                    clanWarAttacks = CoC_War.getCurrentClanAttacks(CoC_PROC.retrieveDataSupercellAPI(Commands.COCWAROPPONENT.getDefaultURL(), cocApiKey));
                    opponentWarAttacks = CoC_War.getCurrentOpponentAttacks(CoC_PROC.retrieveDataSupercellAPI(Commands.COCWAROPPONENT.getDefaultURL(), cocApiKey));

                    break;
                case COCOFFLINE:        // Server is offline
                    System.out.println("Server is OFFLINE");
                    break;
                case COCONLINE:         // Server is online
                    System.out.println("Server is ONLINE");
                    break;
                default:
            }
        };

        /* Bepaal de huidige ledenlijst */
        Runnable memberChecker = () -> {
            if (serverStatusCoC == CoC_ServerState.COCONLINE) {
                ArrayList<CoC_PlayerContainer> updatedList = CoC_Clan.getCoCPlayerList(cocApiKey);
                if (!(cocPlayersList.size() == updatedList.size())) {
                    runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(CoC_Clan.getClanChange(cocPlayersList, updatedList)));
                }
                cocPlayersList = updatedList;
            }
        };

        /* Haal de huidige data op van de clanoorlog */
        Runnable warChecker = () -> {
            if (serverStatusCoC == CoC_ServerState.COCONLINE) {
                String warData = CoC_PROC.retrieveDataSupercellAPI(Commands.COCWAROPPONENT.getDefaultURL(), cocApiKey);

                String updatedWarState = CoC_War.getCurrentWarState(warData);

                System.out.println("=========" + new Timestamp(System.currentTimeMillis()) + "=========");
                System.out.println("Warstate: " + warState);
                System.out.println("Updated warstate: " + updatedWarState);

                try {
                    if (warState.equals("preparation") && updatedWarState.equals("inWar")) {
                        String startMessage = CoC_War.warStartMessage(warData);

                        if (!startMessage.isEmpty()) {
                            ArrayList<Long> ids = CoC_PROC.getSubscribedIds();

                            for (long id : ids) {
                                runCommandMessage(new SendMessage().setChatId(id).setText(startMessage).disableNotification());
                            }

                            runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(CoC_War.warStartMessage(startMessage)));
                        }
                    } else if (warState.equals("inWar") && updatedWarState.equals("inWar")) {
                        String warUpdate = CoC_War.warAttacksUpdate(warData, clanWarAttacks, opponentWarAttacks, cocApiKey);
                        String warUpdate3 = CoC_War.war3StarUpdate(warData, clanWarAttacks, cocApiKey);

                        if (!warUpdate.isEmpty()) {
                            ArrayList<Long> ids = CoC_PROC.getSubscribedIds();

                            for (long id : ids) {
                                runCommandMessage(new SendMessage().setChatId(id).setText(warUpdate).disableNotification());
                            }

                            System.out.println(warUpdate);
                            clanWarAttacks = CoC_War.getCurrentClanAttacks(warData);
                            opponentWarAttacks = CoC_War.getCurrentOpponentAttacks(warData);
                        }

                        if (!warUpdate3.isEmpty()) {
                            runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(warUpdate3));
                        }

                        System.out.println("clan attacks: " + clanWarAttacks.size());
                        System.out.println("opponent attacks: " + opponentWarAttacks.size());

                    } else if (warState.equals("inWar") && updatedWarState.equals("warEnded")) {
                        String warRecap = CoC_War.endWarRecap(warData, cocApiKey);

                        if (!warRecap.isEmpty()) {
                            String endMessage = CoC_War.warEndMessage(warData);

                            if (!endMessage.isEmpty()) {
                                ArrayList<Long> ids = CoC_PROC.getSubscribedIds();

                                for (long id : ids) {
                                    runCommandMessage(new SendMessage().setChatId(id).setText(endMessage).disableNotification());
                                }
                            }

                            runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(warRecap));
                        }
                    } else if (warState.equals("warEnded") && updatedWarState.equals("preparation")) {
                        String foundOpponent = CoC_War.newOpponentOverview(warData);

                        if (!foundOpponent.isEmpty()) {
                            runCommandMessage(new SendMessage().setChatId("315876545").setText(foundOpponent));
                            runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(foundOpponent));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                warState = updatedWarState;
            }
        };

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

        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        executorService.scheduleAtFixedRate(reportGenerator, initalDelay, 1, TimeUnit.DAYS);
        executorService.scheduleAtFixedRate(serverChecker, 0, 1, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(memberChecker, 15, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(warChecker, 45, 60, TimeUnit.SECONDS);
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
        String apiUser = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Api_user FROM credentials WHERE Api = 'telegram'");

            while (rs.next()) {
                apiUser = rs.getString("Api_user");
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return apiUser;
    }

    /**
     * @return  bot token
     */
    public String getBotToken() {
        String apiKey = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Api_key FROM credentials WHERE Api = 'telegram'");

            while (rs.next()) {
                apiKey = rs.getString("Api_key");
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return apiKey;
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

        System.out.println("=========" + new Timestamp(System.currentTimeMillis()) + "=========");
        System.out.println(Arrays.toString(log));
    }

    /**
     * Stuur aan de hand van een SendMessage een bericht terug naar een gebruiker/groep
     *
     * @param sendMessage   Bericht wat terug gestuurd moet worden
     */
    private void runCommandMessage(SendMessage sendMessage) {
        try {
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            execute(sendMessage);
        } catch (TelegramApiException tea) {
            tea.printStackTrace();
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
            tea.printStackTrace();
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
                if (serverStatusCoC == CoC_ServerState.COCONLINE) {
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCCLANMEMBERSTOFILE.getCommand())) {
                        if (cmdBuilder.getCommands().length > 1 && cmdBuilder.getCommands()[1].startsWith("#")) {
                            cmdBuilder.getCommands()[1] = cmdBuilder.getCommands()[1].startsWith("#") ? "%23" + cmdBuilder.getCommands()[1].substring(1) : cmdBuilder.getCommands()[1];

                            SendDocument sendDocumentrequest = new SendDocument();
                            sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                            sendDocumentrequest.setNewDocument(CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getEditableURL() + cmdBuilder.getCommands()[1] + "/members", false, cocApiKey));
                            sendDocumentrequest.setCaption("Clanleden overzicht");
                            runCommandDocument(sendDocumentrequest);
                            break COMMAND_CONTROL;
                        }
                        if (cmdBuilder.getCommands().length > 1 && !cmdBuilder.getCommands()[1].startsWith("#")) {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Om het commando uit te voeren heb ik na het commando nog een parameter nodig.\nBijvoorbeeld: /cocclanmembersfile #joc9cpy"));
                            runCommandMessage(cmdBuilder.getSendMessage());
                            break COMMAND_CONTROL;
                        } else {
                            SendDocument sendDocumentrequest = new SendDocument();
                            sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                            sendDocumentrequest.setNewDocument(CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), false, cocApiKey));
                            sendDocumentrequest.setCaption("Clanleden overzicht");
                            runCommandDocument(sendDocumentrequest);
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCBLACKLISTADD.getCommand())) {
                        if (cmdBuilder.getCommands().length < 3) {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Om het commando goed uit te kunnen voeren heb ik een spelerstag en een reden nodog waarom de speler op de zwarte lijst moet staan"));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        } else {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Blacklist.addToCOCBlacklist(cmdBuilder.getCommands()[1], Arrays.copyOfRange(cmdBuilder.getCommands(), 2, cmdBuilder.getCommands().length), cocApiKey));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCBLACKLISTREMOVE.getCommand())) {
                        if (cmdBuilder.getCommands().length == 2) {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Blacklist.removeFromCOCBlacklist(cmdBuilder.getCommands()[1]));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        } else {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Om het commando goed uit te kunnen voeren heb ik nog een spelerstag nodig"));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCBLACKLISTVIEW.getCommand())) {
                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(CoC_Blacklist.getBlacklist());
                        sendDocumentrequest.setCaption("Blacklist");
                        runCommandDocument(sendDocumentrequest);
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCBLACKLISTCHECK.getCommand())) {
                        if (cmdBuilder.getCommands().length == 2) {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_Blacklist.checkBlacklistPlayer(cmdBuilder.getCommands()[1]));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        } else {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Om het commando goed uit te kunnen voeren heb ik nog een spelerstag nodig"));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCWAROPPONENT.getCommand())) {
                        if (cmdBuilder.getCommands().length == 1) {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_War.getCurrentOpponentOverview(CoC_PROC.retrieveDataSupercellAPI(Commands.COCWAROPPONENT.getDefaultURL(), cocApiKey)));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCBASSIEAWARD.getCommand())) {
                        if (cmdBuilder.getCommands().length == 1) {
                            cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_War.getBassieAwardTopDrie(clanWarAttacks, warState, cocApiKey));
                            runCommandMessage(cmdBuilder.getSendMessage());
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCWARSUBSCRIBE.getCommand())) {
                        if (cmdBuilder.getCommands().length == 1) {
                            if (cmdBuilder.getChatID() > 0) {
                                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_War.subscribeToWarEvents(cmdBuilder.getChatID()));
                                runCommandMessage(cmdBuilder.getSendMessage());
                            } else {
                                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Dit commando is niet beschikbaar in een groepsgesprek!"));
                                runCommandMessage(cmdBuilder.getSendMessage());
                            }
                        }
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].startsWith(Commands.COCWARUNSUBSCRIBE.getCommand())) {
                        if (cmdBuilder.getCommands().length == 1) {
                            if (cmdBuilder.getChatID() > 0) {
                                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_War.unsubscribeToWarEvents(cmdBuilder.getChatID()));
                                runCommandMessage(cmdBuilder.getSendMessage());
                            } else {
                                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Dit commando is niet beschikbaar in een groepsgesprek!"));
                                runCommandMessage(cmdBuilder.getSendMessage());
                            }
                        }
                        break COMMAND_CONTROL;
                    }
                } else {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Dit commando kan niet worden uitgevoerd wanneer de server offline is..."));
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                //////     Einde Clash of Clans commando's   //////

                ///////////////////////////////////////////////////
                //////          NS/trein commando's          //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getLocatieCommands()[0].startsWith(Commands.TREINTIJDEN.getCommand())) {
                    if (cmdBuilder.getLocatieCommands().length > 1) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NS_Vertrektijden.getVertrektijden(nsApi, cmdBuilder.getLocatieCommands()[1]));
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } else {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Om het commando uit te voeren heb ik de naam van een treinstation in Nederland nodig...\n\nBijvoorbeeld: /treintijden Rotterdam Centraal");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    } break COMMAND_CONTROL;
                }
                if (cmdBuilder.getLocatieCommands()[0].startsWith(Commands.TREINSTORINGEN.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(NS_StoringenWerkzaamheden.getStoringen(nsApi));
                    runCommandMessage(cmdBuilder.getSendMessage()); break COMMAND_CONTROL;
                }
                //////         Einde NS/trein commando's     //////

                ///////////////////////////////////////////////////
                //////          Overige commando's           //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getLocatieCommands()[0].startsWith(Commands.CHAT.getCommand()) && cmdBuilder.getLocatieCommands().length > 1 && cmdBuilder.getChatID() == 315876545) {
                    runCommandMessage(new SendMessage().setChatId(brabantTelegramChatID).setText(cmdBuilder.getLocatieCommands()[1]));
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].startsWith(Commands.HELP.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(H_Help.getHelp());
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].startsWith(Commands.HALLO.getCommand())) {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("Hallo!\nIk ben Inf0B0t\n\nVersie: " + "1.5" + "\n\nStatus: " + (beledigingen ? "Eerlijk" : "Empatisch"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].startsWith(Commands.MODE.getCommand())) {
                    beledigingen = !beledigingen;
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("-- MODE SWITCH --\n" + (beledigingen ? "Empatisch -> Eerlijk" : "Eerlijk -> Empatisch"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].startsWith("/testing")) {
                    try {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(CoC_War.endWarRecap(FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/testwarstats.txt")), cocApiKey));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    runCommandMessage(cmdBuilder.getSendMessage());
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].startsWith("/testwar")) {
                    SendDocument sendDocumentrequest = new SendDocument();
                    sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                    sendDocumentrequest.setNewDocument(new File("/home/thijs/Infobotfiles/testwarstats.txt"));
                    sendDocumentrequest.setCaption("War JSON");
                    runCommandDocument(sendDocumentrequest);
                    break COMMAND_CONTROL;
                }
                if (cmdBuilder.getCommands()[0].startsWith(Commands.LOG.getCommand())) {
                    SendDocument sendDocumentrequest = new SendDocument();
                    sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                    if (SystemUtils.IS_OS_WINDOWS) {
                        sendDocumentrequest.setNewDocument(new File("out/output/output.txt"));
                    } else if (SystemUtils.IS_OS_LINUX) {
                        sendDocumentrequest.setNewDocument(new File("/home/thijs/Infobotfiles/output.txt"));
                    }
                    sendDocumentrequest.setCaption("Log file");
                    runCommandDocument(sendDocumentrequest);
                    break COMMAND_CONTROL;
                }
                //////          Einde overige commando's     //////

                ///////////////////////////////////////////////////
                //////          Roda    commando's           //////
                ///////////////////////////////////////////////////
                if (cmdBuilder.getChatID() == 315876545) {
                    if (cmdBuilder.getCommands()[0].equals("/rodainfo")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(R_Boete.getBoeteLijst());
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodalijst")) {
                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(R_Boete.getAlleBoetes());
                        sendDocumentrequest.setCaption("Boetelijst");
                        runCommandDocument(sendDocumentrequest);
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodalijstopenstaand")) {
                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(R_Boete.getOpenstaandeBoetes());
                        sendDocumentrequest.setCaption("Boetelijst openstaand");
                        runCommandDocument(sendDocumentrequest);
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodalijstbetaald")) {
                        SendDocument sendDocumentrequest = new SendDocument();
                        sendDocumentrequest.setChatId(cmdBuilder.getChatID());
                        sendDocumentrequest.setNewDocument(R_Boete.getBetaaldeBoetes());
                        sendDocumentrequest.setCaption("Boetelijst betaald");
                        runCommandDocument(sendDocumentrequest);
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodaisbetaald")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(R_Boete.setBoeteBetaald(Integer.parseInt(cmdBuilder.getCommands()[1])));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodaisopenstaand")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(R_Boete.setBoeteOpenstaand(Integer.parseInt(cmdBuilder.getCommands()[1])));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodaverwijder")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(R_Boete.verwijderBoete(Integer.parseInt(cmdBuilder.getCommands()[1])));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodaboete")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(R_Boete.voegBoeteToe(Arrays.copyOfRange(cmdBuilder.getCommands(), 1, cmdBuilder.getCommands().length)));
                        runCommandMessage(cmdBuilder.getSendMessage());
                        break COMMAND_CONTROL;
                    }
                    if (cmdBuilder.getCommands()[0].equals("/rodahelp")) {
                        cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText("/rodainfo, /rodalijst, /rodaisbetaald, /rodaisopenstaand, /rodaboete, /rodalijstopenstaand, /rodalijstbetaald, /rodaverwijder");
                        runCommandMessage(cmdBuilder.getSendMessage());
                    }
                }
                //////          Einde roda     commando's     //////
                else {
                    cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(EmojiParser.parseToUnicode(":exclamation:ERROR: Ik ken dit commando helaas niet.\nStuur /help voor beschikbare commando's"));
                    runCommandMessage(cmdBuilder.getSendMessage());
                }

            }
        }

        if (beledigingen) {
            if (cmdBuilder.getMessageText().toLowerCase().contains("homo".toLowerCase()) || cmdBuilder.getMessageText().toLowerCase().contains("gay".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Bam is de grootste homo en Klaas is een stokslobberaar!"));
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("slet".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Ed is een slet!"));
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("hoer".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Hoer?... Volgens mij heb je Bas verkeerd gespeeld"));
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("thijs".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Thijs is echt een topgozer"));
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("hoi".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Hallo gebruiker"));
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("stomme bot".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID()).setText(TextFormatting.toItalic("Ik mag dan wel een bot zijn, maar bots worden gemaakt door mensen") + EmojiParser.parseToUnicode(":heart:"));
                runCommandMessage(cmdBuilder.getSendMessage());
                return;
            }
            if (cmdBuilder.getMessageText().toLowerCase().contains("klaas".toLowerCase()) || cmdBuilder.getMessageText().toLowerCase().contains("duitser".toLowerCase())) {
                cmdBuilder.getSendMessage().setChatId(cmdBuilder.getChatID())
                        .setText(EmojiParser.parseToUnicode(TextFormatting.toItalic(":notes::notes:\n" +
                                "Want Klaas is anders geaard\n" +
                                "Klaas is anders geaard\n" +
                                "Lekker zichzelf, dat is zeker wat waard\n" +
                                "Maar Klaas is anders geaard\n" +
                                ":musical_note::musical_note:")));
                runCommandMessage(cmdBuilder.getSendMessage());
            }
        }
    }
}
