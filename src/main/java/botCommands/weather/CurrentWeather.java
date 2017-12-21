package botCommands.weather;

import org.json.JSONObject;

public class CurrentWeather {
    public static String getCurrentWeather(String urlString) {
        JSONObject json = new JSONObject(Weather_PROC.retrieveDataWeatherAPI(urlString));
        JSONObject current = json.getJSONObject("current_observation");
        JSONObject location = current.getJSONObject("display_location");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append(location.getString("full"));
        botResponse.append(Weather_PROC.weatherIconChecker(current.getString("icon")));
        botResponse.append("\n--------------------------");
        botResponse.append("\nTemperatuur: ").append(current.getDouble("temp_c")).append(" *C");
        botResponse.append("\nGevoelstemperatuur: ").append(current.getDouble("feelslike_c")).append(" *C");
        botResponse.append("\nLuchtvochtigheid: ").append(current.getString("relative_humidity"));

        return String.valueOf(botResponse);
    }
}
