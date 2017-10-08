package info.trongdat.whisperapp.views.timline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Friend;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.adapters.FriendAdapter;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 5/5/2017.
 */

public class UserOptions extends Fragment implements UserPresenterListener {
    private View view;
    RecyclerView lstFriends;
    ArrayList<Friend> listFriends;
    FriendAdapter friendAdapter;
    Socket socket;
    TextView txtFriends;

    private SlackLoadingView loadingView;
    private LinearLayout linearLayout;
    User user;
    UserPresenter userPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_options, container, false);
        userPresenter = new UserPresenter(getActivity(), this);
        user = userPresenter.getSession();
        init();
        return view;
    }

    public void init() {
        userPresenter = new UserPresenter(getActivity(), this);
        user = userPresenter.getSession();
        socket = getIOSocket(getActivity());

        socket.emit("reqfriendsget", user.getUserID());
        socket.on("resfriendsget", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                responseProcess(args);
            }
        });

        txtFriends = (TextView) view.findViewById(R.id.txtFriends);
        lstFriends = (RecyclerView) view.findViewById(R.id.lstFriends);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(false);
        lstFriends.setLayoutManager(linearLayoutManager);

        listFriends = new ArrayList<info.trongdat.whisperapp.models.entities.Friend>();
        friendAdapter = new FriendAdapter(getActivity(), listFriends) {
            @Override
            public void loadMore() {

            }
        };
        lstFriends.setAdapter(friendAdapter);


        linearLayout = (LinearLayout) view.findViewById(R.id.layoutData);
        loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
        loadingView.start();
    }
    public void responseProcess(final Object... args) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        linearLayout.removeView(loadingView);
                        JSONArray jsonArray = new JSONArray(args[0].toString());

                        txtFriends.setText("Friends (" + jsonArray.length() + ")");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            info.trongdat.whisperapp.models.entities.Friend friend = new info.trongdat.whisperapp.models.entities.Friend();
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Log.d("datt", "callmmmmmmmmmmm1: " + obj);
                            friend.setUserID(obj.getInt("userID"));
                            friend.setFriendID(obj.getInt("friendID"));
                            listFriends.add(friend);
                            friendAdapter.notifyDataSetChanged();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
