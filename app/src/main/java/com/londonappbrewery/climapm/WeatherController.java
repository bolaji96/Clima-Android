package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final int REQUEST_CODE = 123;
    // App ID to use OpenWeather data
    final String APP_ID = "be0de80af127301e14365eda9f172cc9";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    //Set LOCATION_PROVIDER:
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    //Declare a LocationManager and a LocationListener:
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the UI elements in the layout to Java code
        //Casting UI elements to findVIewbyID is now redundant
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);

        //Add an OnClickListener to the changeCityButton:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(intent);
            }
        });
    }


    //onResume() override method here:
    @Override
    protected void onResume() {
        super.onResume();
        //Send a message to the Logcat to let us know onResume() has been called
        Log.d("Clima App", "onResume() called.");

        //Create an object of Intent to receive an intent declared in ChangeCityController class
        Intent myIntent = getIntent();
        //Declare a string to receive the extra in the Intent declared in ChangeCityController class
        String city = myIntent.getStringExtra("City");
        //Conditional statement to check if city is not equal to null and call the necessary function
        if (city != null) {
            Log.d("Clima App", "New City: " + city);
            getWeatherForNewCity(city);
        } else {
            Log.d("Clima App", "Getting weather for current location.");
            getWeatherForCurrentLocation();
        }
    }

    //getWeatherForNewCity(String city) method declaration:
    private void getWeatherForNewCity (String city) {
        //Create an object of the RequestParams class
        RequestParams params = new RequestParams();
        //Put the required parameters using the params.put()
        //required parameters are based on the documentation from OpenWeatherMap
        params.put("q", city);
        params.put("appid", APP_ID);
        //method call
        letsDoSomeNetworking(params);
    }

    //getWeatherForCurrentLocation() method declaration
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima App", "onLocationChanged() callback received.");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d("Clima App","Longitude is: " + longitude);
                Log.d("Clima App", "Latitude is: " + latitude);

                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("Clima App", "onProviderDisabled() callback received.");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima App","onRequestPermissionsResult(): Permission granted!");
                getWeatherForCurrentLocation();
            }else {
                Log.d("Clima App", "Permission denied :( ");
            }
        }
    }
// TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Clima app", "Success!! JSON: " + response.toString());

                WeatherDataModel weatherDataModel = WeatherDataModel.fromJSON(response);
                updateUI(weatherDataModel);
            }

            @Override
            public void onFailure (int statusCode, Header [] headers, Throwable e, JSONObject response) {
                Log.e("Clima app", "Fail " + e.toString());
                Log.d("Clima app", "Status code: " + statusCode);
                Toast.makeText(WeatherController.this,"Request Failure",Toast.LENGTH_SHORT).show();
            }
        });
    }


    // TODO: Add updateUI() here:
    private void updateUI (WeatherDataModel weather) {
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        int resourceID = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }


    // TODO: Add onPause() here:
    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
