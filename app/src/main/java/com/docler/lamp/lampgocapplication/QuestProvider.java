package com.docler.lamp.lampgocapplication;

import com.docler.lamp.lampgocapplication.quest.Quest;
import com.docler.lamp.lampgocapplication.utils.HttpSingles;
import com.docler.lamp.lampgocapplication.utils.JsonFunctions;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class QuestProvider {
    private static final Logger LOGGER = Logger.getLogger(QuestProvider.class.getName());

    private final Observable<Collection<Quest>> questObservable;

    public QuestProvider(
            JsonFunctions jsonFunctions,
            HttpSingles httpSingles
    ) {

        Observable.just(1).subscribe(
                (one) -> {
                    Logger.getLogger(this.getClass().getName()).info("test");
                }
        );


        Observable.interval(1, TimeUnit.SECONDS).subscribe(
                (one) -> {
                    Logger.getLogger(this.getClass().getName()).info("test");
                }
        );

        questObservable = Observable
                .interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle(
                        (tick) -> {
                            return httpSingles.get("https://goc-lamp.tk/quest-list");
                        }
                )
                .doOnError(
                        (error) -> {
                            LOGGER.log(Level.WARNING, "Error retrieving quests from server", error);
                        }
                )
                .retry()
                .map(jsonFunctions.convertToMap(Quest.class))
                .doOnError(
                        (error) -> {
                            LOGGER.log(Level.WARNING, "Error converting server response to quests", error);
                        }
                )
                .retry()
                .map(
                        (map) -> {
                            return map.values();
                        }
                )
                .replay(1)
                .autoConnect(0)
                .observeOn(AndroidSchedulers.mainThread())
        ;
    }


    public void registerQuestListener(QuestListener listener) {
        questObservable
                .subscribe(
                        (questList) -> {
                            listener.onQuests(questList);
                        }
                );
    }

    public interface QuestListener {
        void onQuests(Collection<Quest> quests);
    }


}
