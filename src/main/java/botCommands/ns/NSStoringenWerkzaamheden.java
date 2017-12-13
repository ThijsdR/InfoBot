package botCommands.ns;

import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.storingen.Storing;
import nl.pvanassen.ns.model.storingen.Storingen;

import java.io.IOException;

public class NSStoringenWerkzaamheden {

    public static String getStoringen(NsApi nsApi) {

        ApiRequest<Storingen> request = RequestBuilder.getActueleStoringen();
        Storingen storingen = null;

        try {
            storingen = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder actueleStoringen = new StringBuilder();

        assert storingen != null;
        actueleStoringen.append("Actuele storingen:");
        actueleStoringen.append("\n========================");


        if (!storingen.getOngeplandeStoringen().isEmpty()) {
            for (Storing storing : storingen.getOngeplandeStoringen()) {
                actueleStoringen.append("\n<< ").append(storing.getTraject()).append(" >>");
                actueleStoringen.append("\n\n").append(storing.getReden());

                if (!storing.getBericht().isEmpty()) {
                    actueleStoringen.append("\n\n").append(storing.getBericht());
                }

                actueleStoringen.append("\n\nLaatste update: ")
                        .append(String.format("%02d", storing.getDatum().getDay())).append("-")
                        .append(String.format("%02d", storing.getDatum().getMonth())).append("-")
                        .append(String.format("%02d", storing.getDatum().getYear())).append(" ")
                        .append(String.format("%02d", storing.getDatum().getHours())).append(":")
                        .append(String.format("%02d", storing.getDatum().getMinutes()));

                actueleStoringen.append("\n------------------------------------");
            }
        }

        return String.valueOf(actueleStoringen);
    }

    /* NIET GEBRUIKEN!!! */
    public static String getWerkzaamheden(NsApi nsApi) {

        ApiRequest<Storingen> request = RequestBuilder.getGeplandeWerkzaamheden();
        Storingen werkzaamheden = null;

        try {
            werkzaamheden = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder actueleWerkzaamheden = new StringBuilder();

        assert werkzaamheden != null;
        actueleWerkzaamheden.append("Actuele werkzaamheden:");
        actueleWerkzaamheden.append("\n========================");


        if (!werkzaamheden.getGeplandeStoringen().isEmpty()) {
            for (Storing werkzaamheid : werkzaamheden.getGeplandeStoringen()) {
                actueleWerkzaamheden.append("\n<< ").append(werkzaamheid.getTraject()).append(" >>");
                actueleWerkzaamheden.append("\n\n").append(werkzaamheid.getPeriode());

                if (!werkzaamheid.getReden().isEmpty()) {
                    actueleWerkzaamheden.append("\n\n").append(werkzaamheid.getReden());
                }

                if (!werkzaamheid.getAdvies().isEmpty()) {
                    actueleWerkzaamheden.append("\n\n").append(werkzaamheid.getAdvies());
                }

                if (!werkzaamheid.getBericht().isEmpty()) {
                    actueleWerkzaamheden.append("\n\n").append(werkzaamheid.getBericht());
                }

                actueleWerkzaamheden.append("\n------------------------------------");
            }
        }

        return String.valueOf(actueleWerkzaamheden);
    }
}
