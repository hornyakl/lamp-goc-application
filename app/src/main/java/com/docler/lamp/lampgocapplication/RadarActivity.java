package com.docler.lamp.lampgocapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.docler.lamp.lampgocapplication.Quest.Quest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class RadarActivity extends AppCompatActivity {
    private JSONObject questList;
    private String questListJson;
    private String questListPath = "https://goc-lamp.tk/quest-list";

    private LampApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        application = (LampApplication) getApplication();

        Canvas grid = new Canvas(Bitmap.createBitmap(h,w, Bitmap.Config.ARGB_8888));
        grid. drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        grid.drawCircle(w/2, h/2 , w/2, paint);

        AsyncTask<Void, Void, Void> mTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    questListJson = getQuestListFromServer(questListPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                try {
                    questList = new JSONObject(questListJson);

                    Iterator<String> keys = questList.keys();

                    ArrayList<Quest> quests = new ArrayList<>(questList.length());
                    while (keys.hasNext()) {
                        String id = keys.next();
                        JSONObject questObject = questList.getJSONObject(id);
                        quests.add(
                            new Quest(
                                questObject.getInt("id"),
                                questObject.getString("name"),
                                questObject.getString("description"),
                                questObject.getDouble("latitude"),
                                questObject.getDouble("longitude"),
                                questObject.getLong("experience_point")
                            )
                        );
                    }

                    Toast.makeText(RadarActivity.this, "ejjjha", Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mTask.execute();
    }

    private static String getQuestListFromServer(String url) throws IOException {
        BufferedReader inputStream;

        URL jsonUrl = new URL(url);
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
