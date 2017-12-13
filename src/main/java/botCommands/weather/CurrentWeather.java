package botCommands.weather;

import org.json.JSONObject;
import utility.PROC;

public class CurrentWeather {
    public static String getCurrentWeather(String urlString) {
        JSONObject json = new JSONObject(PROC.retrieveDataWeatherAPI(urlString));
        JSONObject current = json.getJSONObject("current_observation");
        JSONObject location = current.getJSONObject("display_location");

        StringBuilder huidigeVoorspelling = new StringBuilder();
        huidigeVoorspelling.append(location.getString("full"));
        huidigeVoorspelling.append(PROC.weatherIconChecker(current.getString("icon")));
        huidigeVoorspelling.append("\n==========================");
        huidigeVoorspelling.append("\nTemperatuur: ").append(current.getDouble("temp_c")).append(" *C");
        huidigeVoorspelling.append("\nGevoelstemperatuur: ").append(current.getDouble("feelslike_c")).append(" *C");
        huidigeVoorspelling.append("\nLuchtvochtigheid: ").append(current.getString("relative_humidity"));

        return String.valueOf(huidigeVoorspelling);
    }
}
