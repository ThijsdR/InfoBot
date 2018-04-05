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

        botResponse.append("_Ik ben er om je te voorzien met relevante data omtrent Clash of Clans.\n\n");
        botResponse.append("Om mij aan te sturen kan je gebruik maken van de volgende commando's:_\n\n");
        botResponse.append("*- Clash of Clans -*\n");
        botResponse.append("/cocclanmembersfile - Een Excel document met alle informatie over de leden van een clan (clantag)\n");
        botResponse.append("/cocblacklistadd - Voeg een speler toe aan de zwarte lijst (spelerstag + reden)\n");
        botResponse.append("/cocblacklistremove - Verwijder een speler van de zwarte lijst (spelerstag)\n");
        botResponse.append("/cocblacklistview - Een tekst bestand met alle spelerstags op de zwarte lijst\n");
        botResponse.append("/cocblacklistcheck - Controleer of de opgegeven speler op de zwarte lijst staat (spelerstag)\n");
        botResponse.append("/cocwaropponent - Overzicht van de huidige tegenstander in de clanoorlog\n");
        botResponse.append("/cocbassie - De huidige top 3 voor de Bassie-award\n");
        botResponse.append("/cocwarsubscribe - Abonneer jezelf op de oorlogsupdates\n");
        botResponse.append("/cocwarunsubscribe - Verwijder jezelf van de oorlogsupdateslijst\n\n");
        botResponse.append("*- Treinen -*\n");
        botResponse.append("/treintijden - Alle vertrekkende treinen vanaf het opgegeven station voor het komende uur (stationsnaam)\n");
        botResponse.append("/treinstoringen - Alle huidige storingen op het spoor\n\n");
        botResponse.append("*- Overig -*\n");
        botResponse.append("/hallo - Begroeting en credits");

        return String.valueOf(botResponse);
    }
}
