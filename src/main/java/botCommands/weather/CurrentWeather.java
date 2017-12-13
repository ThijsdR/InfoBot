package botCommands.weather;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONObject;
import utility.PROC;

public class CurrentWeather {
    public static String getCurrentWeather(String urlString) {
        JSONObject json = new JSONObject(PROC.retrieveDataWeatherAPI(urlString));
        JSONObject current = json.getJSONObject("current_observation");
        JSONObject location = current.getJSONObject("display_location");

        StringBuilder huidigeVoorspelling = new StringBuilder();
        huidigeVoorspelling.append(location.getString("full"));

        String icon = current.getString("icon");

        if (icon.equals("chanceflurries")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :snowflake::question:")); }
        if (icon.equals("chancerain")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :umbrella::question:")); }
        if (icon.equals("chancesleet")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :snowflake::question:")); }
        if (icon.equals("chancesnow")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :snowflake::question:")); }
        if (icon.equals("chancetstorms")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :zap::question:")); }
        if (icon.equals("clear")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :sunny:")); }
        if (icon.equals("cloudy")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :cloud:")); }
        if (icon.equals("flurries")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :snowflake:")); }
        if (icon.equals("fog")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :foggy:")); }
        if (icon.equals("hazy")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :foggy:")); }
        if (icon.equals("mostlycloudy")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :cloud:")); }
        if (icon.equals("mostlysunny")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :partly_sunny:")); }
        if (icon.equals("partlycloudy")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :cloud:")); }
        if (icon.equals("partlysunny")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :partly_sunny:")); }
        if (icon.equals("sleet")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :snowflake:")); }
        if (icon.equals("rain")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :umbrella:")); }
        if (icon.equals("snow")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :snowflake:")); }
        if (icon.equals("sunny")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :sunny:")); }
        if (icon.equals("tstorms")) { huidigeVoorspelling.append(EmojiParser.parseToUnicode(" :zap:")); }

        huidigeVoorspelling.append("\n==========================");
        huidigeVoorspelling.append("\nTemperatuur: ").append(current.getDouble("temp_c")).append(" *C");
        huidigeVoorspelling.append("\nGevoelstemperatuur: ").append(current.getDouble("feelslike_c")).append(" *C");
        huidigeVoorspelling.append("\nLuchtvochtigheid: ").append(current.getString("relative_humidity"));

        return String.valueOf(huidigeVoorspelling);
    }
}
