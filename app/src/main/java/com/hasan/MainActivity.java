package com.hasan;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for (int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String description = jsonPart.getString("description");
                    if (!description.equals("")){
                        message += "Conditions: " + description + "\r\n";
                    }
                }

                JSONObject main = jsonObject.getJSONObject("main");
                String temperature = main.getString("temp");
                message += "Temperature: " + temperature + "°c\n";

                String realFeel = main.getString("feels_like");
                message += "Real feel: " + realFeel + "°c\n";

                String humidity = main.getString("humidity");
                message += "Humidity: " + humidity + "%\n";

                String pressure = main.getString("pressure");
                message += "Pressure: " + pressure + " hPa\n";

                String minTemp = main.getString("temp_min");
                message += "Min temperature: " + minTemp + "°c\n";

                String maxTemp = main.getString("temp_max");
                message += "Max temperature: " + maxTemp + "°c\n";

                String visibility = jsonObject.getString("visibility");
                message += "Visibility: " + visibility + " m\n";

                JSONObject wind = jsonObject.getJSONObject("wind");
                String windSpeed = wind.getString("speed");
                message += "Wind speed: " + windSpeed + " m/s\n";

                String windDirection = wind.getString("deg");
                message += "Wind direction: " + windDirection + "°\n";

                JSONObject clouds = jsonObject.getJSONObject("clouds");
                String cloudCover = clouds.getString("all");
                message += "Cloud cover: " + cloudCover + "%\n";

                resultTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                resultTextView.setText(message);
            }
            catch (Exception e) {
                e.printStackTrace();
                resultTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                resultTextView.setText("\n\n\n\n\n\n\n\n\n\n\n\nCould not find weather");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void getWeather(View view){
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02");
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }
}