package info.trongdat.whisperapp.views.bot;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.views.libs.FloatingTextButton;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;

public class Bot extends Fragment {
    View view;

    SlackLoadingView loadingView;
    LinearLayout layoutData;
    FloatingTextButton btnStart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bot, container, false);

        init();
        return view;
    }

    public void init() {

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Simsimi Bot");
        btnStart = (FloatingTextButton) view.findViewById(R.id.btnStart);
        layoutData = (LinearLayout) view.findViewById(R.id.layoutData);
        loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
        loadingView.start();

        new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                layoutData.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
        }.start();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Explode explode = new Explode();
                explode.setDuration(500);
                getActivity().getWindow().setExitTransition(explode);
                getActivity().getWindow().setEnterTransition(explode);
//                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                Intent i2 = new Intent(getActivity(), AutoConversation.class);
                getActivity().startActivity(i2);
            }
        });


    }
}
