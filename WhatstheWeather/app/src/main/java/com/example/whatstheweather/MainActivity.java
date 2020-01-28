package com.example.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
{

    EditText cityEditText;
    Button weatherButton;
    TextView resultTextView;
    TextView locationTextView;
    ImageView imageView;
    String description;
    String main;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = (EditText) findViewById(R.id.cityEditText);
        weatherButton = (Button) findViewById(R.id.weatherButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        locationTextView = (TextView)findViewById(R.id.locationTextView);

    }

    public void getWeather(View view)
    {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");

            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");

            /*To hide the keyboard once the button is clicked*/
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);

        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            String result = "";

            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            }
            catch(Exception e){
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{

                JSONObject jsonObject = new JSONObject(s);

                JSONObject mainObject = jsonObject.getJSONObject("main");
                JSONObject sysObject = jsonObject.getJSONObject("sys");

                String weatherInfo = jsonObject.getString("weather");

                String place = jsonObject.getString("name");
                String country = sysObject.getString("country");

                locationTextView.setText(place + ", "+ country );

                Log.i("Weather content",weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                double temp = mainObject.getDouble("temp");
                double pressure = mainObject.getDouble("pressure");
                double humidity = mainObject.getDouble("humidity");

                String message = "";

                for(int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    Log.i("Weather", jsonPart.getString("main"));

                    if (!main.equals("") && !description.equals("") && temp != 0.0) {
                        message += "Today: "+ main + "(" + description + ") " + "\r\n" + "Temperature: "+temp +
                                " °C" + "\r\n" + "Pressure: " + pressure + " hPa" + "\r\n" + "Humidity: " + humidity +"%" ;
                    }
                }
                if(!message.equals("")){
                    resultTextView.setText(message);
                }else {
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

        }
    }
}
