package ns;

import com.vdurmont.emoji.EmojiParser;
import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.storingen.Storing;
import nl.pvanassen.ns.model.storingen.Storingen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class NS_StoringenWerkzaamheden {

    public static String getStoringen(NsApi nsApi) {

        ApiRequest<Storingen> request = RequestBuilder.getActueleStoringen();
        Storingen storingen = null;

        try {
            storingen = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder botResponse = new StringBuilder();

        assert storingen != null;
        botResponse.append("Actuele storingen:");
        botResponse.append("\n------------------------\n");


        if (!storingen.getOngeplandeStoringen().isEmpty()) {
            for (Storing storing : storingen.getOngeplandeStoringen()) {
                botResponse.append("<< ").append(storing.getTraject()).append(" >>");
                botResponse.append("\n\n").append(storing.getReden());

                if (!storing.getBericht().isEmpty()) {
                    botResponse.append("\n\n").append(storing.getBericht());
                }

                botResponse.append("\n\nLaatste update: ")
                        .append(String.format("%02d", storing.getDatum().getDate())).append("-")
                        .append(String.format("%02d", storing.getDatum().getMonth() + 1)).append("-")
                        .append(String.format("%02d", storing.getDatum().getYear() - 100 + 2000)).append(" ")
                        .append(String.format("%02d", storing.getDatum().getHours())).append(":")
                        .append(String.format("%02d", storing.getDatum().getMinutes()));

                botResponse.append("\n-~-~-~-~-~-~-~-~\n");
            }
        } else {
            botResponse.append(EmojiParser.parseToUnicode("\nEr zijn momenteel GEEN storingen :grimacing::muscle:"));
        }

        return String.valueOf(botResponse);
    }

    public static File getWerkzaamheden(NsApi nsApi) {
        File werkzaamhedenFile = new File("werkzaamheden.txt");

        ApiRequest<Storingen> request = RequestBuilder.getGeplandeWerkzaamheden();
        Storingen werkzaamheden = null;

        try {
            werkzaamheden = nsApi.getApiResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Actuele werkzaamheden:");
        botResponse.append("\n------------------------n");

        assert werkzaamheden != null;
        if (!werkzaamheden.getGeplandeStoringen().isEmpty()) {
            for (Storing werkzaamheid : werkzaamheden.getGeplandeStoringen()) {
                botResponse.append("<< ").append(werkzaamheid.getTraject()).append(" >>");
                botResponse.append("\n\n").append(werkzaamheid.getPeriode());

                if (!werkzaamheid.getReden().isEmpty()) {
                    botResponse.append("\n\n").append(werkzaamheid.getReden());
                }

                if (!werkzaamheid.getAdvies().isEmpty()) {
                    botResponse.append("\n\n").append(werkzaamheid.getAdvies());
                }

                if (!werkzaamheid.getBericht().isEmpty()) {
                    botResponse.append("\n\n").append(werkzaamheid.getBericht());
                }

                botResponse.append("\n-~-~-~-~-~-~-~-~\n");
            }
        }

        try {
            FileUtils.writeStringToFile(werkzaamhedenFile, botResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return werkzaamhedenFile;
    }
}