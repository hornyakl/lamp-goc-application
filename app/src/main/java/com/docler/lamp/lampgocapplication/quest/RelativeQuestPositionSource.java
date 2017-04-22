package com.docler.lamp.lampgocapplication.quest;

import android.location.Location;

import com.docler.lamp.lampgocapplication.matrix.IQuaternion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Function4;

public class RelativeQuestPositionSource extends Observable<Collection<RelativeQuestPosition>> {
    private static final double EARTH_RADIUS = 6371000;

    private final Observable<Collection<RelativeQuestPosition>> delegate;

    public RelativeQuestPositionSource(
            Observable<Collection<Quest>> questSource,
            Observable<Location> locationSource,
            Observable<IQuaternion> orientationSource
    ) {

        delegate = Observable
                .interval(0, 200, TimeUnit.MILLISECONDS)
                .withLatestFrom(
                        questSource,
                        locationSource,
                        orientationSource,
                        new Function4<Long, Collection<Quest>, Location, IQuaternion, Collection<RelativeQuestPosition>>() {
                            @Override
                            public Collection<RelativeQuestPosition> apply(
                                    Long tick,
                                    Collection<Quest> quests,
                                    Location location,
                                    IQuaternion quaternion
                            ) throws Exception {
                                return createRelativeQuests(quests, location, quaternion);
                            }
                        }
                );
    }

    private Collection<RelativeQuestPosition> createRelativeQuests(
            Collection<Quest> quests,
            Location location,
            IQuaternion quaternion) {
        double ownAngle = quaternion.toEulerAngles()[0];

        List<RelativeQuestPosition> results = new ArrayList<>(quests.size());
        for (Quest quest : quests) {
            double distance = getDistance(
                    location.getLatitude(),
                    location.getLongitude(),
                    quest.getLatitude(),
                    quest.getLongitude()
            );

            double angle = getAngle(
                    location.getLatitude(),
                    location.getLongitude(),
                    quest.getLatitude(),
                    quest.getLongitude()
            );

            results.add(new RelativeQuestPosition(angle - ownAngle, distance, quest));
        }

        return results;
    }


    private double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (EARTH_RADIUS * c);
    }

    private double getAngle(double lat1, double lng1, double lat2, double lng2) {
        return Math.atan2(lat2 - lat1, lng2 - lng1);
    }

    @Override
    protected void subscribeActual(Observer<? super Collection<RelativeQuestPosition>> observer) {
        delegate.subscribe(observer);
    }
}
