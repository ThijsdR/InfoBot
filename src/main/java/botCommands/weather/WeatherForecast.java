package botCommands.weather;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherForecast {
    public static String getForecast(String urlString) {
        JSONObject json = new JSONObject(Weather_PROC.retrieveDataWeatherAPI(urlString));
        JSONObject forecast = json.getJSONObject("forecast");
        JSONObject txt = forecast.getJSONObject("txt_forecast");
        JSONArray forecastArray = txt.getJSONArray("forecastday");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n\n");
        botResponse.append("Voorspelling:");
        botResponse.append("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < forecastArray.length(); i++) {
            botResponse.append("\n\n");
            botResponse.append(forecastArray.getJSONObject(i).getString("title")).append(Weather_PROC.weatherIconChecker(forecastArray.getJSONObject(i).getString("icon")));
            botResponse.append("\n------------------------");
            botResponse.append("\n").append(forecastArray.getJSONObject(i).getString("fcttext_metric"));
        }

        return String.valueOf(botResponse);
    }
}
