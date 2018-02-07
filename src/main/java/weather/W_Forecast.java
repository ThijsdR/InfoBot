package weather;

import org.json.JSONArray;
import org.json.JSONObject;

public class W_Forecast {
    public static String getForecast(String urlString) {
        JSONObject json = new JSONObject(W_PROC.retrieveDataWeatherAPI(urlString));
        JSONObject forecast = json.getJSONObject("forecast");
        JSONObject txt = forecast.getJSONObject("txt_forecast");
        JSONArray forecastArray = txt.getJSONArray("forecastday");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Voorspelling:");
        botResponse.append("\n--------------------------");

        for (int i = 0; i < forecastArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append(forecastArray.getJSONObject(i).getString("title")).append(W_PROC.weatherIconChecker(forecastArray.getJSONObject(i).getString("icon")));
            botResponse.append("\n-~-~-~-~-~-~-~-~");
            botResponse.append("\n").append(forecastArray.getJSONObject(i).getString("fcttext_metric"));
            botResponse.append("\n");
        }

        return String.valueOf(botResponse);
    }
}
