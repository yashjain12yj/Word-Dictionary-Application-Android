package dictionary.yashjain12yj.com.dictionary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t;
    Button btnsrch;
    Button btnspk;
    EditText et;
    String finalJSON;

    String meaning;
    String word;
    private TextView tv;
    String defination;
    String example;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnsrch = (Button) findViewById(R.id.button);
        btnspk = (Button) findViewById(R.id.button2);
        et = (EditText) findViewById(R.id.editText);
        tv = (TextView) findViewById(R.id.textView);

        btnsrch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // get Connectivity Manager object to check connection
                ConnectivityManager connec =
                        (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

                // Check for network connections
                if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                        connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

                    // if connected with internet

                    word = et.getText().toString();

                    String url1 = "http://api.urbandictionary.com/v0/define?term=" + word;

                    new JSONTask().execute(url1);
                    if (defination != null && example != null) {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText("Defination : " + defination + "\nExample : " + example);
                        tv.setVisibility(View.VISIBLE);
                    }
                    else{
                        tv.setText("No Defination Found \nor\nTry Again \nor \nInternet not working");
                    }

                } else if (
                        connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("No Internet Connection");

                }





            }
        });


        t = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t.setLanguage(Locale.UK);
                }
            }
        });


        btnspk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaning = tv.getText().toString();
                t.speak(meaning, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }


    public class JSONTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;

            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                finalJSON=buffer.toString();

                JSONObject parentobject= new JSONObject(finalJSON);
                JSONArray parentarray = parentobject.getJSONArray("list");
                JSONObject finalobject = parentarray.getJSONObject(0);
                defination = finalobject.getString("definition");
                example = finalobject.getString("example");



                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (connection != null)
                    connection.disconnect();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            meaning = result;
        }
    }

}













