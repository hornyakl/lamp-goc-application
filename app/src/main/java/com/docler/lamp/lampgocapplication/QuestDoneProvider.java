package com.docler.lamp.lampgocapplication;

import android.os.AsyncTask;

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

public class QuestDoneProvider {

    public void acceptQuest(Quest quest)
    {
        new QuestTask(quest).execute();
    }


    private class QuestTask extends AsyncTask<Void, Void, Void> {
        QuestTask(Quest quest) {
            questListPath += "?quest_id=" + quest.getId();
        }

        private JSONObject questList;
        private String questListJson;
        private String questListPath = "https://goc-lamp.tk/quest-accepted";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                questListJson = getQuestListFromServer(questListPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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
