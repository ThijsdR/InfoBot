package ns;

import com.vdurmont.emoji.EmojiParser;
import help.H_Help;
import nl.pvanassen.ns.ApiRequest;
import nl.pvanassen.ns.NsApi;
import nl.pvanassen.ns.RequestBuilder;
import nl.pvanassen.ns.model.storingen.Storing;
import nl.pvanassen.ns.model.storingen.Storingen;
import utility.TextFormatting;

import java.io.IOException;

public class NS_StoringenWerkzaamheden
{

    public static String getStoringen(NsApi nsApi)
    {

        ApiRequest<Storingen> request = RequestBuilder.getActueleStoringen();
        Storingen storingen = null;

        try
        {
            storingen = nsApi.getApiResponse(request);
        } catch (IOException e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        StringBuilder botResponse = new StringBuilder();

        assert storingen != null;
        botResponse.append(TextFormatting.toBold("Actuele storingen:"));
        botResponse.append("\n------------------------\n");


        if (!storingen.getOngeplandeStoringen().isEmpty())
        {
            for (Storing storing : storingen.getOngeplandeStoringen())
            {
                botResponse.append(TextFormatting.toItalic(storing.getTraject()));
                botResponse.append("\n\n").append(storing.getReden());

                if (!storing.getBericht().isEmpty())
                {
                    botResponse.append("\n\n").append(storing.getBericht());
                }

                botResponse.append("\n\n<code>Laatste update: ")
                        .append(String.format("%02d", storing.getDatum().getDate())).append("-")
                        .append(String.format("%02d", storing.getDatum().getMonth() + 1)).append("-")
                        .append(String.format("%02d", storing.getDatum().getYear() - 100 + 2000)).append(" ")
                        .append(String.format("%02d", storing.getDatum().getHours())).append(":")
                        .append(String.format("%02d", storing.getDatum().getMinutes())).append("</code>");

                botResponse.append(TextFormatting.toBold("\n-~-~-~-~-~-~-~-~\n"));
            }
        } else
        {
            botResponse.append(EmojiParser.parseToUnicode(TextFormatting.toItalic("\nEr zijn momenteel GEEN storingen :grimacing::muscle:")));
        }

        return String.valueOf(botResponse);
    }
}
