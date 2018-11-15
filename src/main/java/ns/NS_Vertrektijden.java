package ns;

import com.vdurmont.emoji.EmojiParser;
import help.H_Help;
import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.vertrektijden.VertrekkendeTrein;
import nl.pvanassen.ns.model.vertrektijden.VertrekkendeTreinen;
import utility.TextFormatting;

import java.io.IOException;


public class NS_Vertrektijden
{
    /**
     * @param nsApi   API gegevens
     * @param station Station waarvoor de vertrektijden opgevraagd worden
     * @return String met alle vertrektijden van het opgegeven station
     */
    public static String getVertrektijden(NsApi nsApi, String station)
    {

        ApiRequest<VertrekkendeTreinen> request = RequestBuilder.getActueleVertrektijden(station);
        VertrekkendeTreinen vertrekkendeTreinen = null;

        try
        {
            vertrekkendeTreinen = nsApi.getApiResponse(request);
        } catch (IOException e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        StringBuilder botResponse = new StringBuilder();
        botResponse.append(EmojiParser.parseToUnicode("Station :station:: ")).append(TextFormatting.toItalic(station));
        botResponse.append("\n------------------------");

        assert vertrekkendeTreinen != null;
        for (VertrekkendeTrein trein : vertrekkendeTreinen)
        {
            botResponse.append("\n").append(trein.getTreinSoort());
            botResponse.append("\n-> ").append(trein.getEindBestemming());
            botResponse.append("\nSpoor: ").append(trein.getVertrekSpoor());
            botResponse.append("\n").append(String.format("%02d", trein.getVertrekTijd().getHours()))
                    .append(":").append(String.format("%02d", trein.getVertrekTijd().getMinutes()));

            if (trein.getVertrekVertragingMinuten() != 0)
            {
                botResponse.append(" <code>+").append(EmojiParser.parseToUnicode(trein.getVertrekVertragingMinuten() + "</code>:exclamation:"));
            }

            if (trein.isGewijzigdVertrekspoor())
            {
                botResponse.append(TextFormatting.toBold("\n\nGewijzigd vertrekspoor!"));
            }

            if (!trein.getOpmerkingen().isEmpty())
            {
                for (String opmerking : trein.getOpmerkingen())
                {
                    botResponse.append("\n").append(TextFormatting.toBold(opmerking));
                }
            }

            botResponse.append("\n-~-~-~-~-~-~-~-~-~-~-~-~");
        }

        return String.valueOf(botResponse);
    }
}
