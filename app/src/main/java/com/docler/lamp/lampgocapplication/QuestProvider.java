package com.docler.lamp.lampgocapplication;

import android.os.AsyncTask;
import android.os.Handler;

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
import java.util.List;

public class QuestProvider {



    private List<QuestListener> listeners;

    private List<Quest> quests;

    private Handler handler;

    public QuestProvider() {
        this.listeners = new ArrayList<>();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new QuestTask().execute();

                //Do something after 100ms
                handler.postDelayed(this, 2000);
            }
        }, 0);
    }

    public void registerQuestListener(QuestListener listener)
    {
        listeners.add(listener);
    }

    public List<Quest> getQuests()
    {
        return quests;
    }

    public interface QuestListener
    {
        void onQuests (List<Quest> quests);
    }

    private class QuestTask extends AsyncTask<Void, Void, Void> {
        private JSONObject questList;
        private String questListJson;
        private String questListPath = "https://goc-lamp.tk/quest-list";

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

                quests = new ArrayList<>(questList.length());

                while (keys.hasNext()) {
                    String id = keys.next();
                    JSONObject questObject = questList.getJSONObject(id);
                    Quest quest = new Quest(
                            questObject.getInt("id"),
                            questObject.getString("name"),
                            questObject.getString("description"),
                            questObject.getDouble("latitude"),
                            questObject.getDouble("longitude"),
                            questObject.getLong("experience_point")
                    );

                    quests.add(
                            quest
                    );
                }

                for (QuestListener listener : listeners)
                {
                    listener.onQuests(quests);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


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
}
