/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.XML;

public class Service {
    String kraj;
    Gson gson = new Gson();
    String City;
    String Waluta;

    public String getKraj() {
        return kraj;
    }

    public String getCity() {
        return City;
    }

    public String getWaluta() {
        return Waluta;
    }

    public Service(String kraj) {
        this.kraj = kraj;
    }


    public String getWeather(String miasto) throws IOException {
        City = miasto;

        String apiKey = ""; //your api key
        String geolocationString = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                miasto, apiKey);
        InputStream inputStream = new URL(geolocationString).openConnection().getInputStream();
        String locationCollector = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        Location[] locations = gson.fromJson(locationCollector, Location[].class);
        if (locations.length == 0) {
            throw new IOException("Nie znaleziono lokalizacji dla miasta: " + miasto);
        }
        double latitude = locations[0].lat;
        double longitude = locations[0].lon;
        String weatherString = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%.6f&lon=%.6f&appid=%s&units=metric",
                latitude, longitude, apiKey
        );
        inputStream = new URL(weatherString).openConnection().getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
    }


    public double getRateFor(String kod_waluty) throws IOException {
        Waluta = kod_waluty;
        Currency currency = getCodeFromCuntry(kraj);
        String apiKey = ""; //your api key
        String currencyExchange = String.format(
                "https://v6.exchangerate-api.com/v6/%s/latest/%s", apiKey, currency);
        InputStream inputStream = new URL(currencyExchange).openConnection().getInputStream();
        String collectExchangeRates = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        ConversionRates conversionRates = gson.fromJson(collectExchangeRates, ConversionRates.class);
//        for (Map.Entry<String, Double> entry : conversionRates.conversion_rates.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
        return conversionRates.conversion_rates.getOrDefault(kod_waluty, 0.00);
    }

    public Currency getCodeFromCuntry(String country) {
        Currency currency = null;
        for (String countrycode : Locale.getISOCountries()) {
            Locale locale = new Locale("", countrycode);
            if (locale.getDisplayCountry(Locale.ENGLISH).equals(kraj) || locale.getDisplayCountry(new Locale("pl")).equals(country)) {
                try {
                    currency = Currency.getInstance(locale);
                } catch (IllegalArgumentException e) {
                    System.err.println("No currency for this country");
                    e.printStackTrace();
                }
            }

        }
        return currency;
    }

    public double getNBPRate() throws IOException {
        Currency currency = getCodeFromCuntry(kraj);
        if (currency == null) {
            throw new IOException("Nie znaleziono waluty dla kraju: " + kraj);
        }

        String[] urls = {
                "https://static.nbp.pl/dane/kursy/xml/a054z250319.xml",
                "https://static.nbp.pl/dane/kursy/xml/b011z250319.xml"
        };

        String kurs = "";
        for (String url : urls) {
            InputStream inputStream = new URL(url).openConnection().getInputStream();
            String xmlContent = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());


            JSONObject json = XML.toJSONObject(xmlContent);


            if (json.has("tabela_kursow")) {
                JSONObject tabela = json.getJSONObject("tabela_kursow");
                if (tabela.has("pozycja")) {
                    Object pozycje = tabela.get("pozycja");
                    if (pozycje instanceof org.json.JSONArray) {
                        org.json.JSONArray pozycjeArray = (org.json.JSONArray) pozycje;
                        for (int i = 0; i < pozycjeArray.length(); i++) {
                            JSONObject pozycja = pozycjeArray.getJSONObject(i);
                            if (pozycja.getString("kod_waluty").equals(currency.toString())) {
                                kurs = pozycja.getString("kurs_sredni");
                                double przelicznik = pozycja.getDouble("przelicznik");
                                double kursValue = Double.parseDouble(kurs.replace(",", "."));
                                kurs = String.valueOf(kursValue / przelicznik);
                                //1 [waluta] = x PLN
                            }
                        }
                    }
                }
            }
        }

        try {
            return Double.parseDouble(kurs);
        } catch (Exception e) {

            if (getCodeFromCuntry(kraj).toString().equals("PLN"))
            {
                return 1.00;
            }
            else {

                System.err.println("Nie znaleziono przelicznika waluty dla tego kraju: "+kraj);
                return 0.0;
            }
        }
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setWaluta(String waluta) {
        Waluta = waluta;
    }

    public String formatWeather(String jsonWeather) {
        if (jsonWeather == null || jsonWeather.isEmpty()) {
            return "Brak danych pogodowych.";
        }
        try {
            Weather weather = gson.fromJson(jsonWeather, Weather.class);
            if (weather == null) {
                return "Nie udało się sparsować danych pogodowych.";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("=== Pogoda ===\n");
            if (weather.weather != null && !weather.weather.isEmpty()) {
                sb.append("Stan: ").append(weather.weather.get(0).main).append("\n");
                sb.append("Opis: ").append(weather.weather.get(0).description).append("\n");
            } else {
                sb.append("Brak informacji o stanie pogody.\n");
            }
            if (weather.main != null) {
                sb.append("Temperatura: ").append(String.format("%.2f°C", weather.main.temp)).append("\n")
                        .append("Temperatura odczuwalna: ").append(String.format("%.2f°C", weather.main.feels_like)).append("\n")
                        .append("Min. temp: ").append(String.format("%.2f°C", weather.main.temp_min)).append("\n")
                        .append("Max. temp: ").append(String.format("%.2f°C", weather.main.temp_max)).append("\n")
                        .append("Ciśnienie: ").append(String.format("%.1f hPa", weather.main.pressure)).append("\n")
                        .append("Wilgotność: ").append(String.format("%.1f%%", weather.main.humidity)).append("\n");
            } else {
                sb.append("Brak szczegółowych danych o temperaturze.\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Błąd podczas przetwarzania danych pogodowych.";
        }
    }

}




class Weather
{
    @SerializedName("main")
    WeatherMain main;
    @SerializedName("weather")
    List<WeatherInfo> weather;

    @Override
    public String toString() {
        String weatherDescription = weather != null && !weather.isEmpty() ? weather.get(0).toString() : "No weather info";
        return String.format("\n%s\n%s",weatherDescription, main);
    }
}

class WeatherInfo{
    String main;
    String description;

    @Override
    public String toString() {
        return String.format("Condition: %s, Description: %s", main, description);
    }
}

class WeatherMain
{
    double temp;
    double feels_like;
    double temp_min;
    double temp_max;
    double pressure;
    double humidity;

    @Override
    public String toString() {
        return String.format(
                "Temperature: %.2f°C (Feels like: %.2f°C)\nMin Temp: %.2f°C, Max Temp: %.2f°C\nPressure: %.1f hPa, Humidity: %.1f%%",
                temp, feels_like, temp_min, temp_max, pressure, humidity
        );
    }
}

class Location
{
    double lat;
    double lon;
}

class ConversionRates
{
    Map<String, Double> conversion_rates;
}








