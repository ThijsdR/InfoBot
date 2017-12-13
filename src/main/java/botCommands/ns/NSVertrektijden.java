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

        StringBuilder vertrekTijden = new StringBuilder();

        assert vertrekkendeTreinen != null;

        vertrekTijden.append(EmojiParser.parseToUnicode("Station :station:: ")).append(station);
        vertrekTijden.append("\n========================");

        for (VertrekkendeTrein trein : vertrekkendeTreinen) {
            vertrekTijden.append("\n").append(trein.getTreinSoort());
            vertrekTijden.append("\n-> ").append(trein.getEindBestemming());
            vertrekTijden.append("\nSpoor: ").append(trein.getVertrekSpoor());
            vertrekTijden.append("\n").append(String.format("%02d", trein.getVertrekTijd().getHours()))
                    .append(":").append(String.format("%02d", trein.getVertrekTijd().getMinutes()));

            if (trein.getVertrekVertragingMinuten() != 0) {
                vertrekTijden.append(" +").append(EmojiParser.parseToUnicode(trein.getVertrekVertragingMinuten() + ":exclamation:"));
            }

            if (trein.isGewijzigdVertrekspoor()) {
                vertrekTijden.append("\n\n-> Gewijzigd vertrekspoor! <-");
            }

            if (!trein.getOpmerkingen().isEmpty()) {
                for (String opmerking : trein.getOpmerkingen()) {
                    vertrekTijden.append("\n").append(opmerking);
                }
            }

            vertrekTijden.append("\n-~-~-~-~-~-~-~-~-~-~-~-~");
        }

        return String.valueOf(vertrekTijden);
    }
}
