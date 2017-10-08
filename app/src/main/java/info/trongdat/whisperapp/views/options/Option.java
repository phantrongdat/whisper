package info.trongdat.whisperapp.views.options;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Timeline;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.services.async.AsyncNewPost;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import info.trongdat.whisperapp.views.user.Login;

/**
 * Created by Alone on 3/16/2017.
 */

public class Option extends Fragment implements UserPresenterListener, View.OnClickListener {
    View view;

    User user;
    UserPresenter userPresenter;
    LinearLayout btnLogout;
    SlackLoadingView loadingView;
    LinearLayout layoutData;
    CircleImageView imgAvatar;
    TextView txtName, txtNewPost;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_option, container, false);
        userPresenter = new UserPresenter(getActivity(), this);
        user = userPresenter.getSession();
        init();
        return view;
    }

    public void init() {
        try {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle("Options");

            txtName = (TextView) view.findViewById(R.id.txtName);
            txtNewPost = (TextView) view.findViewById(R.id.txtNewPost);
            imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
            Timeline newPost = new AsyncNewPost(user.getUserID()).execute().get();
            Picasso.with(getActivity()).load(user.getAvatar())
                    .into(imgAvatar);

            txtName.setText(user.getFullName());
            txtNewPost.setText(newPost.getText());
            btnLogout = (LinearLayout) view.findViewById(R.id.btnLogout);
            layoutData = (LinearLayout) view.findViewById(R.id.layoutData);
            layoutData.setVisibility(View.GONE);
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
            btnLogout.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                userPresenter.createSession(new User(0, "", "", "", "", "", "", "", "", ""));
                Explode explode = new Explode();
                explode.setDuration(500);
                getActivity().getWindow().setExitTransition(explode);
                getActivity().getWindow().setEnterTransition(explode);
//                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                Intent i2 = new Intent(getActivity(), Login.class);
                getActivity().startActivity(i2);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public void sessionSuccess(String name) {

    }

    @Override
    public void sessionFail() {

    }

    @Override
    public void attemptSuccess() {

    }

    @Override
    public void attemptFail(String err) {

    }
}

