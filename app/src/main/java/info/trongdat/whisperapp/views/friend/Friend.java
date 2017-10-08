package info.trongdat.whisperapp.views.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Contact;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.FriendPresenter;
import info.trongdat.whisperapp.presenters.IComponents;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.adapters.FriendAdapter;
import info.trongdat.whisperapp.presenters.adapters.SuggestionAdapter;
import info.trongdat.whisperapp.presenters.services.async.AsyncFriendsSuggestion;
import info.trongdat.whisperapp.presenters.services.async.AsyncInfo;
import info.trongdat.whisperapp.utils.VerifyPermissions;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Constants.ACTION_FRIEND_REQUEST_TRUE;
import static info.trongdat.whisperapp.utils.Constants.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 3/16/2017.
 */

public class Friend extends Fragment implements IComponents, View.OnClickListener, UserPresenterListener {
    private View view;
    private SwipeRefreshLayout refreshLayout;
    RelativeLayout btnFriends, btnFriendsSuggestion;
    ImageButton btnFriendsScan;
    TextView txtFriends;
    boolean showFriends = true;
    RecyclerView lstFriends, lstFriendsSuggestion;
    ArrayList<info.trongdat.whisperapp.models.entities.Friend> listFriends;
    ArrayList<User> listSuggestion;
    FriendAdapter friendAdapter;
    SuggestionAdapter suggestionAdapter;
    Socket socket;

    private SlackLoadingView loadingView;
//    private LinearLayout linearLayout;
    User user;

    UserPresenter userPresenter;
    FriendPresenter friendPresenter;
    ArrayList<Contact> contacts;
    BroadcastReceiver bcrRequestFriend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);

        VerifyPermissions.verify(getActivity());
        init();
        return view;
    }


    @Override
    public void init() {
        friendPresenter = new FriendPresenter(getActivity());
        setHasOptionsMenu(true);
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

        socket.on("resfriendadd", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                responseProcess(args);
            }
        });
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Friends");
        txtFriends = (TextView) view.findViewById(R.id.txtFriends);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_blue_bright));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        refreshLayout.setRefreshing(false);
                    }
                };
                countDownTimer.start();
            }
        });

        btnFriends = (RelativeLayout) view.findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(this);
        btnFriendsScan = (ImageButton) view.findViewById(R.id.btnFriendsScan);
        btnFriendsScan.setOnClickListener(this);
        btnFriendsSuggestion = (RelativeLayout) view.findViewById(R.id.btnFriendsSuggestion);
        btnFriendsSuggestion.setOnClickListener(this);

        lstFriends = (RecyclerView) view.findViewById(R.id.lstFriends);
        lstFriendsSuggestion = (RecyclerView) view.findViewById(R.id.lstFriendsSuggestion);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(false);
        lstFriends.setLayoutManager(linearLayoutManager);
        lstFriends.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        lstFriendsSuggestion.setLayoutManager(linearLayoutManager2);
        lstFriendsSuggestion.setNestedScrollingEnabled(false);
        listFriends = new ArrayList<info.trongdat.whisperapp.models.entities.Friend>();
        listSuggestion = new ArrayList<User>();
        friendAdapter = new FriendAdapter(getActivity(), listFriends) {
            @Override
            public void loadMore() {

            }
        };
        lstFriends.setAdapter(friendAdapter);

        suggestionAdapter = new SuggestionAdapter(getActivity(), listSuggestion) {
            @Override
            public void itemClick(View v, int position) {
                if (v.getId() == R.id.btnAddFriend)
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userID", userPresenter.getSessionID());
                        jsonObject.put("friendID", listSuggestion.get(position).getUserID());
                        Log.d(TAG, "onClick: " + jsonObject);
                        socket.emit("reqfriendadd", jsonObject);
                        listSuggestion.remove(position);
                        suggestionAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };
        lstFriendsSuggestion.setAdapter(suggestionAdapter);

//        linearLayout = (LinearLayout) view.findViewById(R.id.layoutData);
        loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
        loadingView.start();
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                getSuggestion();
            }
        }.start();
        bcrRequestFriend = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processRequestFriend(context, intent);
            }
        };
        getActivity().registerReceiver(bcrRequestFriend, new IntentFilter(ACTION_FRIEND_REQUEST_TRUE));
    }

    private void processRequestFriend(Context context, final Intent intent) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String result = intent.getExtras().getString("result");
                    loadingView.setVisibility(View.GONE);
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        Log.d(TAG, "processRequestFriend: " + result);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSuggestion() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    contacts = friendPresenter.getContacts();
                    for (int i = 0; i < contacts.size(); i++) {
                        try {
                            String result = new AsyncFriendsSuggestion(user.getUserID(), contacts.get(i).getPhoneNumber()).execute().get();
                            if (result.equals("false")) {
                                final User friends = new AsyncInfo(contacts.get(i).getPhoneNumber()).execute().get();
                                listSuggestion.add(friends);
                                try {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            suggestionAdapter.notifyDataSetChanged();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFriends:
                if (showFriends) {
                    lstFriends.setVisibility(View.VISIBLE);
                } else {
                    lstFriends.setVisibility(View.GONE);
                }
                showFriends = !showFriends;
                break;
            case R.id.btnFriendsSuggestion:
                if (showFriends) {
                    lstFriendsSuggestion.setVisibility(View.VISIBLE);
                } else {
                    lstFriendsSuggestion.setVisibility(View.GONE);
                }
                showFriends = !showFriends;
                break;
            case R.id.btnFriendsScan:
                Explode explode = new Explode();
                explode.setDuration(500);
                getActivity().getWindow().setExitTransition(explode);
                getActivity().getWindow().setEnterTransition(explode);
//                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                Intent i2 = new Intent(getActivity(), FindScan.class);
                getActivity().startActivity(i2);
                break;
        }

    }

    private void responseAddFriendProcess(final Object... args) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        info.trongdat.whisperapp.models.entities.Friend friend = new info.trongdat.whisperapp.models.entities.Friend();
                        JSONObject obj = new JSONObject(args[0].toString());
                        Log.d("datt", "callmmmmmmmmmmm1: " + obj);
                        friend.setUserID(obj.getInt("userID"));
                        friend.setFriendID(obj.getInt("friendID"));
                        listFriends.add(friend);
                        friendAdapter.notifyDataSetChanged();

                        txtFriends.setText("Friends (" + listFriends.size() + ")");
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

    public void responseProcess(final Object... args) {
        try {
            Intent intent = new Intent();
            intent.setAction(ACTION_FRIEND_REQUEST_TRUE);
            intent.putExtra("result", args[0].toString());
            getActivity().sendBroadcast(intent);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadingView.clearAnimation();
        loadingView.reset();
        getActivity().unregisterReceiver(bcrRequestFriend);
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
