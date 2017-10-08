package info.trongdat.whisperapp.views.timline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;

/**
 * Created by Alone on 3/16/2017.
 */

public class UserAlbumView extends Fragment implements UserPresenterListener {
    View view;
    // Store instance variables
    private String title;
    private int page;
    User user;
    UserPresenter userPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_album, container, false);
        userPresenter = new UserPresenter(getActivity(), this);
        user = userPresenter.getSession();

        return view;
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
