package botCommands.ns;

import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.vertrektijden.VertrekkendeTrein;
import nl.pvanassen.ns.model.vertrektijden.VertrekkendeTreinen;

import java.io.IOException;

public class NSVertrektijden {
    public static String getVertrektijden(NsApi nsApi, String station) {

        ApiRequest<VertrekkendeTreinen> request = RequestBuilder.getActueleVertrektijden(station);
        VertrekkendeTreinen vertrekkendeTreinen = null;

        try {
            vertrekkendeTreinen = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder tijden = new StringBuilder();

        assert vertrekkendeTreinen != null;
        for (VertrekkendeTrein trein : vertrekkendeTreinen) {
            tijden.append(trein.getTreinSoort()).append(" -- ").append(trein.getVertrekTijd().getHours()).append(":").append(trein.getVertrekTijd().getMinutes()).append("\tVertraging: ").append(trein.getVertrekVertragingMinuten()).append("\n");
        }

        return String.valueOf(tijden);
    }
}
