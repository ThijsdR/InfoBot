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

        StringBuilder stationslijst = new StringBuilder();

        assert stations != null;

        stationslijst.append("Stations:");
        stationslijst.append("\n========================");

        for (Station station : stations) {
            stationslijst.append("\n").append(station.getNamen().getMiddel());
        }

        return String.valueOf(stationslijst);
    }
}
