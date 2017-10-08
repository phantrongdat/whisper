package info.trongdat.whisperapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.utils.Internet;
import info.trongdat.whisperapp.utils.VerifyPermissions;
import info.trongdat.whisperapp.views.bot.Bot;
import info.trongdat.whisperapp.views.conversation.Home;
import info.trongdat.whisperapp.views.friend.Friend;
import info.trongdat.whisperapp.views.libs.spacenavigation.SpaceItem;
import info.trongdat.whisperapp.views.libs.spacenavigation.SpaceNavigationView;
import info.trongdat.whisperapp.views.libs.spacenavigation.SpaceOnClickListener;
import info.trongdat.whisperapp.views.notification.Notification;
import info.trongdat.whisperapp.views.options.Option;
import info.trongdat.whisperapp.views.user.UserView;
import io.socket.client.Socket;

import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

public class AppMain extends AppCompatActivity {

    @InjectView(R.id.spaceNavigation)
    SpaceNavigationView spaceNavigation;
    @InjectView(R.id.container)
    FrameLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);
        ButterKnife.inject(this);
        initialize(savedInstanceState);
    }

    public void initialize(Bundle savedInstanceState) {
        spaceNavigation.initWithSaveInstanceState(savedInstanceState);
        spaceNavigation.addSpaceItem(new SpaceItem("Timeline", R.drawable.ic_timeline));
        spaceNavigation.addSpaceItem(new SpaceItem("Friends", R.drawable.ic_account2_black_24dp));
        spaceNavigation.setCentreButtonIcon(R.drawable.ic_message);
        spaceNavigation.addSpaceItem(new SpaceItem("Simsimi", R.drawable.ic_simsimi_white));
        spaceNavigation.addSpaceItem(new SpaceItem("Options", R.drawable.ic_list_black_24dp));
        spaceNavigation.showIconOnly();
        spaceNavigation.shouldShowFullBadgeText(true);
//        spaceNavigation.showBadgeAtIndex(3, 6, Color.RED);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new Home()).commit();

        spaceNavigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
//                Toast.makeText(AppMain.this, "onCentreButtonClick", Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new Home()).commit();

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
//                Toast.makeText(AppMain.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (itemIndex) {
                    case 0:
                        fragmentTransaction.replace(R.id.container, new UserView()).commit();
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.container, new Friend()).commit();
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.container, new Bot()).commit();
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.container, new Option()).commit();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
//                Toast.makeText(AppMain.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (itemIndex) {
                    case 0:
                        fragmentTransaction.replace(R.id.container, new UserView()).commit();
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.container, new Friend()).commit();
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.container, new Notification()).commit();
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.container, new Option()).commit();
                        break;
                }
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    VerifyPermissions.verify(AppMain.this);
                    JSONObject update = new JSONObject();
                    Location location = new Internet(AppMain.this).myLocation();
                    UserPresenter userPresenter = new UserPresenter(AppMain.this);
                    User user = userPresenter.getSession();
                    socket = getIOSocket(AppMain.this);

                    update.put("userID", user.getUserID());
                    update.put("lastLocation", location.getLatitude() + "," + location.getLongitude());
                    socket.emit("userlocation", update);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //    private static final int TIME_DELAY = 2000;
//    private static long backpressed;
//
//    @Override
//    public void onBackPressed() {
//
//        if (backpressed + TIME_DELAY > System.currentTimeMillis()) {
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
//            moveTaskToBack(true);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
//        } else {
//            Toast.makeText(getApplicationContext(), "Press again to exit!",
//                    Toast.LENGTH_SHORT).show();
//        }
//        backpressed = System.currentTimeMillis();
//    }
    Socket socket;

    @Override
    protected void onResume() {
        socket = getIOSocket(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigation.onSaveInstanceState(outState);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
}
