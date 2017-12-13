package botCommands.weather;

import org.json.JSONArray;
import org.json.JSONObject;
import utility.PROC;

public class WeatherForecast {
    public static String getForecast(String urlString) {
        JSONObject json = new JSONObject(PROC.retrieveDataWeatherAPI(urlString));
        JSONObject forecast = json.getJSONObject("forecast");
        JSONObject txt = forecast.getJSONObject("txt_forecast");
        JSONArray forecastArray = txt.getJSONArray("forecastday");

        StringBuilder botResponse = new StringBuilder();

        botResponse.append("Voorspelling:");
        botResponse.append("\n================");

        for (int i = 0; i < forecastArray.length(); i++) {
            botResponse.append("\n\n");
            botResponse.append(forecastArray.getJSONObject(i).getString("title")).append(PROC.weatherIconChecker(forecastArray.getJSONObject(i).getString("icon")));
            botResponse.append("\n------------------------");
            botResponse.append("\n").append(forecastArray.getJSONObject(i).getString("fcttext_metric"));
        }

        return String.valueOf(botResponse);
    }
}
