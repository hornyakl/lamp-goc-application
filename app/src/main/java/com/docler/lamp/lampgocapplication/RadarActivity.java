package com.docler.lamp.lampgocapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RadarActivity extends AppCompatActivity {
    private JSONObject questList;
    private String questListJson;
    private String questListPath = "https://goc-lamp.tk/quest-list";

    private LampApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        application = (LampApplication) getApplication();

        AsyncTask<Void, Void, Void> mTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try {
                    questListJson = getQuestListFromServer(questListPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);

                try {
                    JSONObject questList = new JSONObject(questListJson);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mTask.execute();
    }

    private static String getQuestListFromServer(String url) throws IOException
    {
        BufferedReader inputStream;

        URL jsonUrl      = new URL(url);
        URLConnection dc = jsonUrl.openConnection();

        dc.setConnectTimeout(1000);
        dc.setReadTimeout(1000);

        inputStream = new BufferedReader(
            new InputStreamReader(
                dc.getInputStream()
            )
        );

        return inputStream.readLine();
    }

    @Override
    protected void onPause() {
        application.stopViewChangeListen();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        application.startViewChangeListen(this);
    }


}
