package help;

import utility.TextFormatting;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Deze klasse bevat methode(s) met betrekking tot het /help commando
 */
public class H_Help
{

    /**
     * Deze methode geeft een overzicht terug van alle huidige beschikbare commando's van Inf0_B0t
     *
     * @return Een overzicht van alle beschikbare commando's
     */
    public static String getHelp()
    {
        StringBuilder botResponse = new StringBuilder();

        botResponse.append(TextFormatting.toItalic("Ik ben er om je te voorzien met relevante data omtrent Clash of Clans.\n\n Om mij aan te sturen kan je gebruik maken van de volgende commando's:\n\n"));
        botResponse.append(TextFormatting.toBold("- Clash of Clans -\n"));
        botResponse.append("/blacklistadd - Voeg een speler toe aan de zwarte lijst (spelerstag + reden)\n");
        botResponse.append("/blacklistremove - Verwijder een speler van de zwarte lijst (spelerstag)\n");
        botResponse.append("/blacklistview - Een tekst bestand met alle spelerstags op de zwarte lijst\n");
        botResponse.append("/blacklistcheck - Controleer of de opgegeven speler op de zwarte lijst staat (spelerstag)\n");
        botResponse.append("/war - Overzicht van de huidige tegenstander in de clanoorlog\n");
        botResponse.append("/time - Laat zien hoe lang de oorlog nog bezig is\n");
        botResponse.append("/bassie - De huidige top 3 voor de Bassie-award\n");
        botResponse.append("/subscribe - Abonneer jezelf op de oorlogsupdates\n");
        botResponse.append("/unsubscribe - Verwijder jezelf van de oorlogsupdateslijst\n\n");
        botResponse.append(TextFormatting.toBold("- Treinen -\n"));
        botResponse.append("/treintijden - Alle vertrekkende treinen vanaf het opgegeven station voor het komende uur (stationsnaam)\n");
        botResponse.append("/treinstoringen - Alle huidige storingen op het spoor\n\n");
        botResponse.append(TextFormatting.toBold("- Overig -\n"));
        botResponse.append("/hallo - Begroeting en credits");

        return String.valueOf(botResponse);
    }

    /***
     *
     * @param e
     * @return
     */
    public static String exceptionStacktraceToString(Exception e)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }
}
