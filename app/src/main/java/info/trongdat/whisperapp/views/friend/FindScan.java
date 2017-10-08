package info.trongdat.whisperapp.views.friend;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.adapters.ScanAdapter;
import info.trongdat.whisperapp.presenters.services.async.AsyncCheckFriend;
import info.trongdat.whisperapp.presenters.services.async.AsyncUsers;
import info.trongdat.whisperapp.utils.Internet;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

public class FindScan extends AppCompatActivity {
    @InjectView(R.id.lstFriendsScan)
    RecyclerView lstFriends;
    @InjectView(R.id.loading_view)
    SlackLoadingView loadingView;
    @InjectView(R.id.txtLoading)
    TextView txtLoading;
    User user;
    private int userID;
    private UserPresenter userPresenter;
    private Socket socket;

    private ArrayList<User> list, listScan;
    private ScanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_scan);
        ButterKnife.inject(this);
        init();
    }


    public void init() {
        try {
            loadingView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userPresenter = new UserPresenter(this);
        user = userPresenter.getSession();
        socket = getIOSocket(this);
        socket.emit("reqfriendsget", user.getUserID());
        socket.on("resfriendsget", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                responseProcess(args);
            }
        });
        getSupportActionBar().setTitle("Find friends");


        list = new ArrayList<>();
        listScan = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lstFriends.setLayoutManager(linearLayoutManager);
        adapter = new ScanAdapter(this, listScan);
        lstFriends.setAdapter(adapter);

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        list = new AsyncUsers().execute().get();
                        for (final User u : list) {
                            String[] loc = u.getLastLocation().split(",");
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(Double.parseDouble(loc[0]));
                            location.setLongitude(Double.parseDouble(loc[1]));

                            Log.d("dattt", "run: " + location.getLatitude() + "," + location.getLongitude());
                            double distance = new Internet(FindScan.this).distance(location);
                            if (u.getUserID() != user.getUserID() && distance < 5000) {

                                FindScan.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean result = false;
                                        try {
                                            result = new AsyncCheckFriend(user.getUserID(), u.getUserID()).execute().get();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                        if (!result) {
                                            listScan.add(u);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void responseProcess(Object[] args) {

    }

}
