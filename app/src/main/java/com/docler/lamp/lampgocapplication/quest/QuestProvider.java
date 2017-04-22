package com.docler.lamp.lampgocapplication.quest;

import android.location.Location;

import com.docler.lamp.lampgocapplication.utils.http.HttpFunctionsFactory;
import com.docler.lamp.lampgocapplication.utils.http.HttpRequest;
import com.docler.lamp.lampgocapplication.utils.json.JsonFunctionsFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class QuestProvider {
    private static final Logger LOGGER = Logger.getLogger(QuestProvider.class.getName());

    private final JsonFunctionsFactory jsonFunctionsFactory;
    private final HttpFunctionsFactory httpFunctionsFactory;

    public QuestProvider(
            JsonFunctionsFactory jsonFunctionsFactory,
            HttpFunctionsFactory httpFunctionsFactory
    ) {
        this.jsonFunctionsFactory = jsonFunctionsFactory;
        this.httpFunctionsFactory = httpFunctionsFactory;
    }

    public ObservableTransformer<Location, Collection<Quest>> applyQuestProvider() {
        return new ObservableTransformer<Location, Collection<Quest>>() {
            @Override
            public ObservableSource<Collection<Quest>> apply(Observable<Location> upstream) {
                return Observable
                        .interval(0, 2, TimeUnit.SECONDS)
                        .withLatestFrom(
                                upstream,
                                new BiFunction<Long, Location, Location>() {
                                    @Override
                                    public Location apply(Long aLong, Location location) {
                                        return location;
                                    }
                                }
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(
                                new Function<Location, HttpRequest>() {
                                    @Override
                                    public HttpRequest apply(Location location) throws Exception {
                                        return new HttpRequest("https://goc-lamp.tk/quest-list");
                                    }
                                }
                        )
                        .map(httpFunctionsFactory.createGet())
                        .doOnError(
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable error) throws Exception {
                                        LOGGER.log(Level.WARNING, "Error retrieving quests from server", error);
                                    }
                                }
                        )
                        .retry()
                        .map(jsonFunctionsFactory.createJsonToMap(Quest.class))
                        .doOnError(
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable error) throws Exception {
                                        LOGGER.log(Level.WARNING, "Error converting server response to quests", error);
                                    }
                                }
                        )
                        .retry()
                        .map(
                                new Function<Map<String, Quest>, Collection<Quest>>() {
                                    @Override
                                    public Collection<Quest> apply(Map<String, Quest> map) throws Exception {
                                        return map.values();
                                    }
                                }
                        )
                        .replay(1)
                        .refCount();
            }
        };
    }
}
