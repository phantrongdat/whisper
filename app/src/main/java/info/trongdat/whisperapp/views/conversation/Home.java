package info.trongdat.whisperapp.views.conversation;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.MemOfCon;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.adapters.ConversationAdapter;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.ContentValues.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 3/16/2017.
 */

public class Home extends Fragment {
    View view;
    private SwipeRefreshLayout refreshLayout;

    private SlackLoadingView loadingView;
    private LinearLayout linearLayout;

    LinearLayoutManager linearLayoutManager;
    Socket socket;
    User user;
    UserPresenter userPresenter;

    RecyclerView lstConversation;
    ArrayList<MemOfCon> list;
    ConversationAdapter conversationAdapter;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_blue_bright));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        refreshLayout.setRefreshing(false);
                    }
                }.start();
            }
        });
        setHasOptionsMenu(true);
        init();
        return view;
    }

    public void init() {
        try {

            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle("Conversations");
            linearLayout = (LinearLayout) view.findViewById(R.id.layoutData);
            list = new ArrayList<>();
            conversationAdapter = new ConversationAdapter(getActivity(), list) {
                @Override
                public void loadMore() {

                }
            };
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
            lstConversation = (RecyclerView) view.findViewById(R.id.lstConversation);
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
            linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
            lstConversation.setLayoutManager(linearLayoutManager2);
            lstConversation.setAdapter(conversationAdapter);
            loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
            loadingView.start();

            userPresenter = new UserPresenter(getActivity());
            user = userPresenter.getSession();

            socket = getIOSocket(getActivity());

            JSONObject jsonObject = new JSONObject();
            Log.d(TAG, "init: " + user.getUserID());
            jsonObject.put("userID", user.getUserID());
            socket.emit("req_get_conversation", jsonObject);
            socket.on("res_get_conversation", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    processGetConversations(args);
                }
            });
            socket.on("res_add_member", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    processNewConversations(args);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processGetConversations(final Object[] args) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.GONE);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(args[0].toString());
                    Log.d(TAG, "processRequest: " + args[0].toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MemOfCon nemOfCon = new MemOfCon();
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.d("datt", "callmmmmmmmmmmm1: " + obj);
                        nemOfCon.setConversationID(obj.getString("conversationID"));
                        nemOfCon.setUserID(obj.getInt("userID"));
                        nemOfCon.setLastUpdate(obj.getString("lastUpdate"));
                        list.add(0, nemOfCon);
                        conversationAdapter.notifyDataSetChanged();
                    }
                    conversationAdapter.rearrangeByDate();
//                    conversationAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void processNewConversations(final Object[] args) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {

                    MemOfCon memOfCon = new MemOfCon();
                    JSONObject obj = new JSONObject(args[0].toString());
                    Log.d("datt", "callmmmmmmmmmmm1: " + obj);

//                    ArrayList<User> mems = new AsyncMemOfCon(obj.getInt("conversationID")).execute().get();
//                    boolean ofCon = false;
//                    for (User user1 : mems) {
//                        if (user1.getUserID() == user.getUserID()) ofCon = true;
//                    }
//                    if (ofCon) {
                    memOfCon.setConversationID(obj.getString("conversationID"));
                    memOfCon.setUserID(user.getUserID());
                    list.add(0, memOfCon);
                    conversationAdapter.notifyDataSetChanged();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_conversation, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmAddConversation:
                NewConversationDialog newConversation = new NewConversationDialog();
                newConversation.show(getFragmentManager(), "New conversation", toolbar);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
