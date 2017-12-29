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
        botResponse.append("/cocclanmembers - Leden binnen een clan (clantag)\n");
        botResponse.append("/cocclanmember - Informatie over een clanlid (clantag + naam)\n");
        botResponse.append("/cocclanmembersfile - Een Excel document met alle informatie over de leden van een clan (clantag)\n\n");
        botResponse.append("- Clash Royale -\n");
        botResponse.append("/crclaninfo - Informatie over de opgegeven clan (clantag)\n");
        botResponse.append("/crclanmembers - Leden binnen een clan (clantag)\n");
        botResponse.append("/crclanroles - Rangorde binnen een clan (clantag)\n");
        botResponse.append("/crclandonations - Donaties binnen een clan (clantag)\n");
        botResponse.append("/crclanchest - Behaalde kronen voor de clankist binnen een clan (clantag)\n\n");
        botResponse.append("- Treinen -\n");
        botResponse.append("/treintijden - Alle vertrekkende treinen vanaf het opgegeven station voor het komende uur (stationsnaam)\n");
        botResponse.append("/treinstoringen - Alle huidige storingen op het spoor\n");
        botResponse.append("/treinwerkzaamheden - Alle huidige en geplande werkzaamheden aan het spoor\n\n");
        botResponse.append("- Weer -\n");
        botResponse.append("/weerhuidig - De huidige weersomstandigheden in de opgegeven plaats (plaatsnaam)\n");
        botResponse.append("/weervoorspellingen - De verwachte weersomstandigheden voor de komende vijf dagen (plaatsnaam)\n\n");
        botResponse.append("- Overig -\n");
        botResponse.append("/hallo - Begroeting en credits");

        return String.valueOf(botResponse);
    }
}
