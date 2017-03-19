package com.docler.lamp.lampgocapplication;

import android.os.Handler;

import com.docler.lamp.lampgocapplication.Quest.Quest;
import com.docler.lamp.lampgocapplication.utils.HttpPromiseClient;

import org.jdeferred.android.AndroidDeferredManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuestProvider {
    private final HttpPromiseClient httpPromiseClient;
    private final Handler handler;

    private List<QuestListener> listeners;

    private List<Quest> quests;

    public QuestProvider() {

        // todo: use dependency injection
        final AndroidDeferredManager deferredManager = new AndroidDeferredManager();
        httpPromiseClient = new HttpPromiseClient(deferredManager);
        handler = new Handler();

        this.listeners = new ArrayList<>();

        handler.post(new Runnable() {
            @Override
            public void run() {
                httpPromiseClient.get(
                        "https://goc-lamp.tk/quest-list"
                ).done(
                        (result) -> {
                            handleQuestListResult(result);
                        }
                ).fail(
                        (throwable) -> {
                            // todo: logging
                        }
                );

                handler.postDelayed(this, 2000);
            }
        });
    }

    private void handleQuestListResult(String questListJson) {
        try {
            JSONObject questList = new JSONObject(questListJson);

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

            for (QuestListener listener : listeners) {
                listener.onQuests(quests);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registerQuestListener(QuestListener listener) {
        listeners.add(listener);
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public interface QuestListener {
        void onQuests(List<Quest> quests);
    }
}
