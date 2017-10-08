package info.trongdat.whisperapp.views.user;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.services.async.AsyncInfo;
import info.trongdat.whisperapp.utils.Internet;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Constants.ACTION_SIGNUP_SUCCESS;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;
import static info.trongdat.whisperapp.utils.Utils.md5Encode;

public class SignUp extends AppCompatActivity implements UserPresenterListener {
    @InjectView(R.id.fabClose)
    FloatingActionButton fabClose;
    @InjectView(R.id.cavSignUp)
    CardView cavSignUp;

    @InjectView(R.id.edtPhoneNo)
    EditText edtPhoneNo;
    @InjectView(R.id.edtPassword)
    EditText edtPassword;
    @InjectView(R.id.edtRePassword)
    EditText edtRePassword;
    @InjectView(R.id.edtFullName)
    EditText edtFullName;
    @InjectView(R.id.edtAddress)
    EditText edtAddress;
    @InjectView(R.id.edtBirthDay)
    TextView edtBirthDay;
    @InjectView(R.id.edtEmail)
    EditText edtEmail;
    @InjectView(R.id.edtIntro)
    EditText edtIntro;
    @InjectView(R.id.cbxTermAgree)
    CheckBox cbxTermAgree;
    @InjectView(R.id.btnComplete)
    ActionProcessButton btnComplete;

    Socket socket;
    BroadcastReceiver brcSignupListener;
    String result;
    Handler handler = new Handler();
    Runnable task;
    UserPresenter userPresenter;

    View focusView;
    TextView txtToastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.getWindow().setStatusBarColor(Color.parseColor("#FF2B83AA"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }

        socket = getIOSocket(this);

        if (!new Internet(this).getState()) {

            txtToastMessage.setText("No internet!");
            Toast toast = Toast.makeText(this, "Internet", Toast.LENGTH_LONG);
            toast.setView(focusView);
            toast.show();
        }
        init();
    }


    public void init() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        userPresenter = new UserPresenter(this, this);
        focusView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);
        txtToastMessage = (TextView) focusView.findViewById(R.id.txtText1);

        btnComplete.setMode(ActionProcessButton.Mode.ENDLESS);
        brcSignupListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiveProcess(intent);
            }
        };
        registerReceiver(brcSignupListener, new IntentFilter(ACTION_SIGNUP_SUCCESS));

        socket.on("signupresponse", listener);
    }

    Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                final JSONObject response = (JSONObject) args[0];

                Log.d("datt", "call: " + response);
                String result = response.get("result").toString();

                Intent intent = new Intent();
                intent.setAction(ACTION_SIGNUP_SUCCESS);
                intent.putExtra("result", result);
                sendBroadcast(intent);


            } catch (RuntimeException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @OnClick({R.id.btnComplete, R.id.fabClose, R.id.edtBirthDay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabClose:
                animateRevealClose();
                break;
            case R.id.btnComplete:
                username = edtPhoneNo.getText().toString();
                password = edtPassword.getText().toString();
                rePassword = edtRePassword.getText().toString();
                fullName = edtFullName.getText().toString();
                address = edtAddress.getText().toString();
                birthDay = edtBirthDay.getText().toString();
                email = edtEmail.getText().toString();
                intro = edtIntro.getText().toString();

//                username = edtUsername.getText().toString();
//                password = edtPassword.getText().toString();
                if (cbxTermAgree.isChecked())
                    userPresenter.attemptSignUp(username, password, rePassword, fullName, address, birthDay, email, intro);
                else {
                    txtToastMessage.setText("Term is require agree!");
                    Toast toast = Toast.makeText(this, "Internet", Toast.LENGTH_LONG);
                    toast.setView(focusView);
                    toast.show();
                }
                break;

            case R.id.edtBirthDay:
                DatePickerBuilder dpb = new DatePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                        .setYearOptional(true).addDatePickerDialogHandler(new DatePickerDialogFragment.DatePickerDialogHandler() {
                            @Override
                            public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
                                edtBirthDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        });
                dpb.show();
                break;
        }
    }

    String username, password, rePassword, fullName, address, birthDay, email, intro;

    @Override
    public void attemptSuccess() {
        btnComplete.setEnabled(false);
        final int mProgress = 0;
        task = new Runnable() {
            @Override
            public void run() {
                // do something
                btnComplete.setProgress(mProgress + 10);
                handler.postDelayed(this, 1000);

            }

        };
        task.run();
        try {
            JSONObject object = new JSONObject();
            object.put("phoneNumber", username);
            object.put("password", md5Encode(password));
            object.put("fullName", fullName);
            object.put("address", address);
            object.put("birthDay", birthDay);
            object.put("email", email);
            object.put("intro", intro);
            Location location = new Internet(SignUp.this).myLocation();
            object.put("lastLocation", location.getLatitude() + "," + location.getLongitude());
            Log.d("datt", "onClick: " + "\n" + object);
            socket.emit("signup", object);

            User user = new AsyncInfo(username).execute().get();
            JSONObject timeline = new JSONObject();
            timeline.put("userID", user.getUserID());
            timeline.put("text", "Đã tham gia Whisper!");
            timeline.put("data", "null");
            timeline.put("time", new Date().toString());

            socket.emit("reqtimelineadd", timeline);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attemptFail(String err) {
        txtToastMessage.setText(err);
        Toast toast = Toast.makeText(this, "Attempt Login", Toast.LENGTH_LONG);
        toast.setView(focusView);
        toast.show();
    }

    public void receiveProcess(Intent intent) {
        if (intent.getAction() == ACTION_SIGNUP_SUCCESS) {
            result = intent.getExtras().getString("result");
            Log.d("datt", "call: " + result);
            Toast toast = Toast.makeText(SignUp.this, "Result SignUp", Toast.LENGTH_LONG);
            toast.setView(focusView);
            if (result.equals("true")) {
                animateRevealClose();
                txtToastMessage.setText("Sign up is successfully, please Login!");
            }
            if (result.equals("false"))
                txtToastMessage.setText("Cannot sign up your user, try again!");
            if (result.equals("existed")) txtToastMessage.setText("The phone number is existed!");


            toast.show();
            handler.removeCallbacks(task);
            btnComplete.setEnabled(true);
            handler.removeCallbacks(task);
            btnComplete.setText("COMPLETE");
            btnComplete.setProgress(0);
        }

    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cavSignUp.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cavSignUp, cavSignUp.getWidth() / 2, 0, fabClose.getWidth() / 2, cavSignUp.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cavSignUp.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cavSignUp, cavSignUp.getWidth() / 2, 0, cavSignUp.getHeight(), fabClose.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cavSignUp.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fabClose.setImageResource(R.drawable.plus);
                SignUp.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brcSignupListener);
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    @Override
    public void sessionSuccess(String name) {

    }

    @Override
    public void sessionFail() {

    }

}
