package info.trongdat.whisperapp.views.user;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.adapters.ViewPagerAdapter;
import info.trongdat.whisperapp.views.timline.TimelineView;
import info.trongdat.whisperapp.views.timline.UserAboutView;
import info.trongdat.whisperapp.views.timline.UserOptions;
import io.socket.client.Socket;

/**
 * Created by Alone on 3/16/2017.
 */

public class UserView extends Fragment implements UserPresenterListener {
    private View view;
    private TabLayout tabUser;
    private int userID;
    private UserPresenter userPresenter;
    private Socket socket;
    private User user;
    private ActionBar actionBar;
    private BroadcastReceiver brcResponse;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    FrameLayout layoutUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        init();
//        brcResponse = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction() == ACTION_GETUSER_SUCCESS) {
//                    User resUser = (User) intent.getSerializableExtra("user");
//                    actionBar.setTitle(resUser.getFullName());
//                }
//            }
//        };
//        getActivity().registerReceiver(brcResponse, new IntentFilter(ACTION_GETUSER_SUCCESS));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void init() {
        userPresenter = new UserPresenter(getActivity(), this);
        userID = userPresenter.getSessionID();
        user = userPresenter.getSession();
        actionBar.setTitle(user.getFullName());

        setHasOptionsMenu(true);

        layoutUser = (FrameLayout) view.findViewById(R.id.layoutUser);
        tabUser = (TabLayout) view.findViewById(R.id.tabUser);
        tabUser.addTab(tabUser.newTab().setText("TIMELINE"));
        tabUser.addTab(tabUser.newTab().setText("PROFILE"));
        tabUser.addTab(tabUser.newTab().setText("FRIENDS"));
//        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
//        pagerAdapter = new ViewPagerAdapter(getFragmentManager());
//        pagerAdapter.addFragment(new TimelineView(), "TIMELINE");
//        pagerAdapter.addFragment(new UserAboutView(), "ABOUT");
//        pagerAdapter.addFragment(new UserAlbumView(), "ALBUMS");
//        viewPager.setAdapter(pagerAdapter);
//        tabUser.setupWithViewPager(viewPager);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.layoutUser, new TimelineView()).commit();

        tabUser.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        fragmentTransaction.replace(R.id.layoutUser, new TimelineView()).commit();
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.layoutUser, new UserAboutView()).commit();
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.layoutUser, new UserOptions()).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
