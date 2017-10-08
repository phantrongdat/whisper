package info.trongdat.whisperapp.views.user;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.trongdat.whisperapp.AppMain;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.utils.Internet;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Constants.ACTION_LOGIN_SUCCESS;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;
import static info.trongdat.whisperapp.utils.Utils.md5Encode;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity implements UserPresenterListener {
    @InjectView(R.id.edtPhoneNo)
    EditText edtUsername;
    @InjectView(R.id.edtPassword)
    EditText edtPassword;
    @InjectView(R.id.btnGo)
    ActionProcessButton btnGo;
    @InjectView(R.id.cavLogin)
    CardView cavLogin;
    @InjectView(R.id.fabSignUp)
    FloatingActionButton fabSignUp;
    @InjectView(R.id.cbxRemember)
    CheckBox cbxRemember;
    @InjectView(R.id.imgBackground)
    ImageView imgBackground;

    Socket socket;
    BroadcastReceiver brcLoginListener;
    String username;
    String password;
    boolean attemptLogin = false;
    Handler handler = new Handler();
    Runnable task;
    UserPresenter userPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        socket = getIOSocket(this);

        if (!new Internet(this).getState()) {
            View focusView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);
            TextView textView = (TextView) focusView.findViewById(R.id.txtText1);

            textView.setText("No internet!");
            Toast toast = Toast.makeText(this, "Internet", Toast.LENGTH_LONG);
            toast.setView(focusView);
            toast.show();
        }
        init();
    }

    public void init() {
        try {

//        Bitmap background = BitmapFactory.decodeResource(this.getResources(),
//                R.drawable.wbackground9);
//        Blurry.with(this)
//                .async()
//                .from(background)
//                .into(imgBackground);
            userPresenter = new UserPresenter(this, this);
            userPresenter.attemptSession();

            btnGo.setMode(ActionProcessButton.Mode.ENDLESS);
            brcLoginListener = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    receiveProcess(intent);
                }
            };
            registerReceiver(brcLoginListener, new IntentFilter(ACTION_LOGIN_SUCCESS));

            socket.on("loginresponse", listener);

//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.getWindow().setStatusBarColor(Color.parseColor("#FF2B83AA"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btnGo, R.id.fabSignUp})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fabSignUp:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fabSignUp, fabSignUp.getTransitionName());
                    startActivity(new Intent(this, SignUp.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, SignUp.class));
                }
                break;
            case R.id.btnGo:
                username = edtUsername.getText().toString();
                password = edtPassword.getText().toString();
                userPresenter.attemptLogin(username, password);
                break;
        }
    }

    Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                final JSONObject response = (JSONObject) args[0];

                Log.d("datt", "call: " + args[0]);
                boolean result = Boolean.parseBoolean(response.get("result").toString());
                User user = new User();

                if (result) {
                    JSONArray jsonArray = response.getJSONArray("user");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    user.setUserID(jsonObject.getInt("userID"));
                    user.setPhoneNumber(jsonObject.getString("phoneNumber"));
                    user.setFullName(jsonObject.getString("fullName"));
                    user.setAddress(jsonObject.getString("address"));
                    user.setAvatar(jsonObject.getString("avatar"));
                    user.setBirthDay(jsonObject.getString("birthDay"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setIntro(jsonObject.getString("intro"));
                    user.setLastLocation(jsonObject.getString("lastLocation"));
                }

                Intent intent = new Intent();
                intent.setAction(ACTION_LOGIN_SUCCESS);
                intent.putExtra("attemptLogin", result);
                intent.putExtra("user", user);
                sendBroadcast(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
//                int userID = Integer.parseInt(response.get("userID").toString());


        }
    };

    public void receiveProcess(Intent intent) {
        if (intent.getAction() == ACTION_LOGIN_SUCCESS) {
            attemptLogin = intent.getExtras().getBoolean("attemptLogin");
            final User user = (User) intent.getExtras().getSerializable("user");
            Log.d("datt", "call: " + attemptLogin);
            if (attemptLogin) {

                Explode explode = new Explode();
                explode.setDuration(500);
                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
//                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                Intent i2 = new Intent(Login.this, AppMain.class);
                startActivity(i2);
                if (cbxRemember.isChecked()) userPresenter.createSession(user);
                else userPresenter.createSession(new User(0, "", "", "", "", "", "", "", "", ""));

            } else {
                View focusView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);
                TextView textView = (TextView) focusView.findViewById(R.id.txtText1);

                textView.setText("The username or password is not correct!");
                Toast toast = Toast.makeText(Login.this, "Result Login", Toast.LENGTH_LONG);
                toast.setView(focusView);
                toast.show();

            }
            handler.removeCallbacks(task);
            edtUsername.setEnabled(true);
            edtPassword.setEnabled(true);
            cbxRemember.setEnabled(true);
            btnGo.setEnabled(true);
            btnGo.setText("GO");
            btnGo.setProgress(0);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brcLoginListener);
    }

    @Override
    public void sessionSuccess(String name) {
        edtUsername.setText(name);
        Intent i2 = new Intent(Login.this, AppMain.class);
        startActivity(i2);
    }

    @Override
    public void sessionFail() {

    }

    @Override
    public void attemptSuccess() {
        edtUsername.setEnabled(false);
        edtPassword.setEnabled(false);
        cbxRemember.setEnabled(false);
        btnGo.setEnabled(false);

        final int mProgress = 0;
        task = new Runnable() {
            @Override
            public void run() {
                // do something
                btnGo.setProgress(mProgress + 10);
                handler.postDelayed(this, 1000);

            }

        };
        task.run();
        try {
            JSONObject object = new JSONObject();
            object.put("phoneNumber", username);
            object.put("password", md5Encode(password));

            Log.d("datt", "onClick: " + "\n" + object);
            socket.emit("login", object);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void attemptFail(String err) {
        View focusView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);
        TextView textView = (TextView) focusView.findViewById(R.id.txtText1);
        textView.setText(err);
        Toast toast = Toast.makeText(this, "Attempt Login", Toast.LENGTH_LONG);
        toast.setView(focusView);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
        moveTaskToBack(true);
    }
}
