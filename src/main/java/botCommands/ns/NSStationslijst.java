package botCommands.ns;

import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.stations.Station;
import nl.pvanassen.ns.model.stations.Stations;

import java.io.IOException;

/**
 * NIET GEBRUIKEN!!!
 */
public class NSStationslijst {
    public static String getStationlijst(NsApi nsApi) {
        ApiRequest<Stations> request = RequestBuilder.getStations();
        Stations stations = null;

        try {
            stations = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Stations:");
        botResponse.append("\n------------------------n");

        assert stations != null;
        for (Station station : stations) {
            botResponse.append(station.getNamen().getMiddel()).append("\n");
        }

        return String.valueOf(botResponse);
    }
}
