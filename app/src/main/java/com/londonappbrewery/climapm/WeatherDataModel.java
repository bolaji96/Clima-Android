package com.londonappbrewery.climapm;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    //member variables here
    private String mTemperature, mCity, mIconName;
    private int mCondition;

    // TODO: Create a WeatherDataModel from a JSON:
    public static WeatherDataModel fromJSON (JSONObject jsonObject) {
        //compulsory try and catch method stub when using JSON
        try {
            //create an object of WeatherDataModel class
            WeatherDataModel weatherData = new WeatherDataModel();
            //call mCity and use the getString function to retrieve "name" from the JSON
            weatherData.mCity = jsonObject.getString("name");
            //call mCondition and use the getString function to retrieve "weather" from the array
            //in the JSON
            weatherData.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            //call mIconName and update the weather icon based on the weatherData.mCondition string
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);
            //Declare a tempResult double variable to store the temperature retrieved from the JSON
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            //round the value of tempResult to a whole number and store in roundedValue
            int roundedValue = (int)Math.rint(tempResult);
            //convert rounded value to string
            weatherData.mTemperature = Integer.toString(roundedValue);
            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    //Create getter methods for temperature, city, and icon name:
    public String getTemperature() {
        //TODO: Get the degree symbol from Google
        return mTemperature + "°";
    }

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }
}
