package roda;

import utility.TextFormatting;

public class R_Help {
    public static String getRodaHelp() {
        return TextFormatting.toItalic("Commando's met betrekking tot de Roda HS boetepot\n\n") +
                TextFormatting.toBold("- /rodainfo -\n") + "Een lijst met alle boetes (codes, omschrijvingen en boetebedragen)\n\n" +
                TextFormatting.toBold("- /rodaboete -\n") + "Voeg een nieuwe boete toe aan de database (format: <commando naam boetecode>)\n\n" +
                TextFormatting.toBold("- /rodaopmerking -\n") + "Voeg een opmerking toe aan de opgegeven boete (format: <commando boeteID opmerking>)\n\n" +
                TextFormatting.toBold("- /rodaverwijder -\n") + "Verwijder een boete uit de database (format: <commando boeteID>)\n\n" +
                TextFormatting.toBold("- /rodalijst -\n") + "Een Exceldocument met alle huidige openstaande en betaalde boetes\n\n" +
                TextFormatting.toBold("- /rodalijstopenstaand -\n") + "Een Exceldocument met alle huidige openstaande boetes\n\n" +
                TextFormatting.toBold("- /rodalijstbetaald -\n") + "Een Exceldocument met alle huidige betaalde boetes\n\n" +
                TextFormatting.toBold("- /rodaisbetaald -\n") + "Zet een openstaande boete in de database op betaald (format: <commando boeteID>)\n\n" +
                TextFormatting.toBold("- /rodaisopenstaand -\n") + "Zet een betaalde boete in de database op openstaand (format: <commando boeteID>)\n\n" +
                TextFormatting.toBold("- /rodaallesbetaald -\n") + "Zet alle boetes van een speler op betaald (format: <commando naam>)\n\n" +
                TextFormatting.toBold("- /rodatotaalopenstaand -\n") + "Een overzicht van het totale openstaande bedrag en het openstaande bedrag per speler\n\n" +
                TextFormatting.toBold("- /rodatotaalbetaald -\n") + "Een overzicht van het totale betaalde bedrag en het betaalde bedrag per speler\n\n" +
                TextFormatting.toBold("- /rodanieuweboete -\n") + "Voeg een nieuwe soort boete toe aan de database welke gegeven kan worden aan spelers (format: <commando boetebedrag kratje(Ja/Nee) omschrijving>)";
    }
}
