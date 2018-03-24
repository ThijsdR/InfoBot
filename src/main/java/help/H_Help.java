package help;

/**
 * Deze klasse bevat methode(s) met betrekking tot het /help commando
 */
public class H_Help {

    /**
     * Deze methode geeft een overzicht terug van alle huidige beschikbare commando's van Inf0_B0t
     *
     * @return      Een overzicht van alle beschikbare commando's
     */
    public static String getHelp() {
        StringBuilder botResponse = new StringBuilder();

        botResponse.append("Ik ben er om je te voorzien met relevante data omtrent Clash of Clans, Clash Royale, treinen en het weer.\n\n");
        botResponse.append("Om mij aan te sturen kan je gebruik maken van de volgende commando's:\n\n");
        botResponse.append("- Clash of Clans -\n");
        botResponse.append("/cocclaninfo - Informatie over de opgegeven clan (naam of clantag)\n");
        botResponse.append("/cocclandonaties - Donaties binnen een clan (clantag)\n");
        botResponse.append("/cocclanmember - Informatie over een clanlid (clantag + naam)\n");
        botResponse.append("/cocclanmembersfile - Een Excel document met alle informatie over de leden van een clan (clantag)\n");
        botResponse.append("/cocblacklistadd - Voeg een speler toe aan de zwarte lijst (spelerstag + reden)\n");
        botResponse.append("/cocblacklistremove - Verwijder een speler van de zwarte lijst (spelerstag)\n");
        botResponse.append("/cocblacklistview - Een tekst bestand met alle spelerstags op de zwarte lijst\n");
        botResponse.append("/cocblacklistcheck - Controleer of de opgegeven speler op de zwarte lijst staat (spelerstag)\n\n");
        botResponse.append("/cocwaropponent - Overzicht van de huidige tegenstander in de clanoorlog");
        botResponse.append("/cocbassie - De huidige top 3 voor de Bassie-award");
        botResponse.append("/cocwarsubscribe - Abonneer jezelf op de oorlogsupdates");
        botResponse.append("/cocwarunsubscribe - Verwijder jezelf van de oorlogsupdateslijst");
        botResponse.append("- Treinen -\n");
        botResponse.append("/treintijden - Alle vertrekkende treinen vanaf het opgegeven station voor het komende uur (stationsnaam)\n");
        botResponse.append("/treinstoringen - Alle huidige storingen op het spoor\n");
        botResponse.append("/treinwerkzaamheden - Alle huidige en geplande werkzaamheden aan het spoor\n\n");
        botResponse.append("- Weer -\n");
        botResponse.append("/weerhuidig - De huidige weersomstandigheden in de opgegeven plaats (plaatsnaam)\n");
        botResponse.append("/weersvoorspellingen - De verwachte weersomstandigheden voor de komende vijf dagen (plaatsnaam)\n\n");
        botResponse.append("- Overig -\n");
        botResponse.append("/hallo - Begroeting en credits");

        return String.valueOf(botResponse);
    }
}
