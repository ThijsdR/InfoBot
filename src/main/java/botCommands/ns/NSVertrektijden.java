package botCommands.ns;

import com.vdurmont.emoji.EmojiParser;
import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.vertrektijden.VertrekkendeTrein;
import nl.pvanassen.ns.model.vertrektijden.VertrekkendeTreinen;

import java.io.IOException;


public class NSVertrektijden {
    /**
     *
     * @param nsApi     API gegevens
     * @param station   Station waarvoor de vertrektijden opgevraagd worden
     * @return          String met alle vertrektijden van het opgegeven station
     */
    public static String getVertrektijden(NsApi nsApi, String station) {

        ApiRequest<VertrekkendeTreinen> request = RequestBuilder.getActueleVertrektijden(station);
        VertrekkendeTreinen vertrekkendeTreinen = null;

        try {
            vertrekkendeTreinen = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n\n");
        botResponse.append(EmojiParser.parseToUnicode("Station :station:: ")).append(station);
        botResponse.append("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        assert vertrekkendeTreinen != null;
        for (VertrekkendeTrein trein : vertrekkendeTreinen) {
            botResponse.append("\n").append(trein.getTreinSoort());
            botResponse.append("\n-> ").append(trein.getEindBestemming());
            botResponse.append("\nSpoor: ").append(trein.getVertrekSpoor());
            botResponse.append("\n").append(String.format("%02d", trein.getVertrekTijd().getHours()))
                    .append(":").append(String.format("%02d", trein.getVertrekTijd().getMinutes()));

            if (trein.getVertrekVertragingMinuten() != 0) {
                botResponse.append(" +").append(EmojiParser.parseToUnicode(trein.getVertrekVertragingMinuten() + ":exclamation:"));
            }

            if (trein.isGewijzigdVertrekspoor()) {
                botResponse.append("\n\n-> Gewijzigd vertrekspoor! <-");
            }

            if (!trein.getOpmerkingen().isEmpty()) {
                for (String opmerking : trein.getOpmerkingen()) {
                    botResponse.append("\n").append(opmerking);
                }
            }

            botResponse.append("\n-~-~-~-~-~-~-~-~-~-~-~-~");
        }

        return String.valueOf(botResponse);
    }
}
