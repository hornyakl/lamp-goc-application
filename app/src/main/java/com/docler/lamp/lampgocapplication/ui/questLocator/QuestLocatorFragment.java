package com.docler.lamp.lampgocapplication.ui.questLocator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.docler.lamp.lampgocapplication.R;
import com.docler.lamp.lampgocapplication.quest.RelativeQuestPosition;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class QuestLocatorFragment extends Fragment {
    private Observable<Collection<RelativeQuestPosition>> relativeQuestPositionSource;

    private Disposable relativeQuestPositionSourceDisposable;

    private CameraView cameraView;
    private RadarView radarView;

    public QuestLocatorFragment() {
        // Required empty public constructor
    }

    public static QuestLocatorFragment newInstance(Observable<Collection<RelativeQuestPosition>> relativeQuestPositionSource) {
        QuestLocatorFragment fragment = new QuestLocatorFragment();

        fragment.relativeQuestPositionSource = relativeQuestPositionSource;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_locator, container, false);
    }

    private void disposeRelativeQuestPositionSource() {
        if (relativeQuestPositionSourceDisposable == null) {
            return;
        }

        if (!relativeQuestPositionSourceDisposable.isDisposed()) {
            relativeQuestPositionSourceDisposable.dispose();
        }

        relativeQuestPositionSourceDisposable = null;
    }

    private void activateRadarView() {
        disposeRelativeQuestPositionSource();
        relativeQuestPositionSourceDisposable = relativeQuestPositionSource.subscribe(
                new Consumer<Collection<RelativeQuestPosition>>() {
                    @Override
                    public void accept(Collection<RelativeQuestPosition> relativeQuestPositions) throws Exception {
                        radarView.setQuestPositions(relativeQuestPositions);
                    }
                }
        );
    }

    private void activateCameraView() {
        disposeRelativeQuestPositionSource();
        relativeQuestPositionSourceDisposable = relativeQuestPositionSource.subscribe(
                new Consumer<Collection<RelativeQuestPosition>>() {
                    @Override
                    public void accept(Collection<RelativeQuestPosition> relativeQuestPositions) throws Exception {
                        cameraView.setQuestPositions(relativeQuestPositions);
                    }
                }
        );
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
