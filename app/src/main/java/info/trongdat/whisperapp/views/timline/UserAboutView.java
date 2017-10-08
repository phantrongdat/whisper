package info.trongdat.whisperapp.views.timline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Timeline;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.adapters.AlbumAdapter;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 3/16/2017.
 */

public class UserAboutView extends Fragment implements UserPresenterListener {
    private View view;

    //    private SwipeRefreshLayout refreshLayout;
    private User user;
    private UserPresenter userPresenter;
    private ImageView imgAvatar;
    private TextView txtName, txtPhone, txtBirthDay, txtEmail, txtAddress, txtIntro;
    private RecyclerView lstAlbum;
    ArrayList<Timeline> list;
    AlbumAdapter myAdapter;
    private Socket socket;

    private SlackLoadingView loadingView;
    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_about, container, false);
        userPresenter = new UserPresenter(getActivity(), this);
        user = userPresenter.getSession();
        init();
        return view;
    }

    public void init() {
        lstAlbum = (RecyclerView) view.findViewById(R.id.lstAlbum);
        StaggeredGridLayoutManager mStaggeredLayoutManager1 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredLayoutManager1.supportsPredictiveItemAnimations();
        mStaggeredLayoutManager1.requestSimpleAnimationsInNextLayout();
        mStaggeredLayoutManager1.isSmoothScrolling();
        lstAlbum.setLayoutManager(mStaggeredLayoutManager1);
        lstAlbum.setNestedScrollingEnabled(false);
        list = new ArrayList<>();
        myAdapter = new AlbumAdapter(getActivity(), list);
        lstAlbum.setAdapter(myAdapter);

//        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
//        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_blue_bright));
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        refreshLayout.setRefreshing(false);
//                    }
//                };
//                countDownTimer.start();
//            }
//        });
        imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtPhone = (TextView) view.findViewById(R.id.txtPhoneNumber);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtBirthDay = (TextView) view.findViewById(R.id.txtBirthDay);
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtIntro = (TextView) view.findViewById(R.id.txtIntro);

        Picasso.with(getActivity()).load(user.getAvatar()).placeholder(R.drawable.anhdaidien).into(imgAvatar);
        txtName.setText(user.getFullName());
        txtPhone.setText(user.getPhoneNumber());
        txtAddress.setText(user.getAddress());
        txtBirthDay.setText(user.getBirthDay());
        txtEmail.setText(user.getEmail());
        txtIntro.setText(user.getIntro());


        linearLayout = (LinearLayout) view.findViewById(R.id.layoutData);
        loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
        loadingView.start();

        socket = getIOSocket(getActivity());

        socket.emit("reqtimelineget", user.getUserID());

        socket.on("restimelineget", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                responseProcess(args);
            }
        });

    }

    private void responseProcess(final Object... args) {
        try {
            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        linearLayout.removeView(loadingView);
//                        if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                        JSONArray jsonArray = new JSONArray(args[0].toString());
//                        list = new ArrayList<Timeline>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objTimeline = jsonArray.getJSONObject(i);
                            Log.d("datt", "callmmmmmmmmmmm: " + objTimeline);
                            Timeline timeline = new Timeline();
                            timeline.setTimelineID(objTimeline.getInt("timelineID"));
                            timeline.setText(objTimeline.getString("text"));
                            timeline.setData(objTimeline.getString("data"));
                            timeline.setDate(objTimeline.getString("time"));
                            if (timeline.getData().contains("http://")) {
                                list.add(0, timeline);
                                myAdapter.notifyDataSetChanged();
                            }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
