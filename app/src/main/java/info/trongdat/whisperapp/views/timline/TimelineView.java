package info.trongdat.whisperapp.views.timline;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.UserPresenterListener;
import info.trongdat.whisperapp.presenters.adapters.TimelineViewAdapter;
import info.trongdat.whisperapp.views.libs.TimelineRow;
import info.trongdat.whisperapp.views.libs.httpconnect.AsyncResponse;
import info.trongdat.whisperapp.views.libs.httpconnect.PostResponseAsyncTask;
import info.trongdat.whisperapp.views.libs.slackloadingview.AnimatorListener;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.app.Activity.RESULT_OK;
import static info.trongdat.whisperapp.utils.Constants.ACTION_GETTIMELINE_SUCCESS;
import static info.trongdat.whisperapp.utils.Constants.ACTION_GETUSER_SUCCESS;
import static info.trongdat.whisperapp.utils.Constants.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;
import static info.trongdat.whisperapp.utils.Utils.bitmapResize;
import static info.trongdat.whisperapp.utils.Utils.getImageBase64;
import static info.trongdat.whisperapp.utils.Utils.getRandomColor;
import static info.trongdat.whisperapp.utils.Utils.getRandomNumber;

/**
 * Created by Admin on 4/12/2017.
 */

public class TimelineView extends Fragment implements UserPresenterListener {
    private View view;
    private RecyclerView lstTimeline;
    //    private PullLoadMoreRecyclerView lstTimeline;
    private ArrayList<TimelineRow> list;
    //    private Set<TimelineRow> templist;
    private TimelineViewAdapter myAdapter;

    private int userID;
    private UserPresenter userPresenter;
    private Socket socket;

    private SwipeRefreshLayout refreshLayout;
    private BroadcastReceiver brcResponse;
    private User user;

    private ActionProcessButton btnPost;
    private EditText edtTimeline;

    private Handler handler = new Handler();
    private Runnable task;
    private ImageButton btnAddPicture;
    private CircleImageView imgAvatar;
    private Bitmap bitmap;
    private SlackLoadingView loadingView;
    private LinearLayout linearLayout;
    boolean showPost = true;
    View viewPost;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timeline, container, false);
//        viewPost = getActivity().getLayoutInflater().inflate(R.layout.layout_post_timeline, null);
        init();
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        brcResponse = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiveProcess(context, intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GETTIMELINE_SUCCESS);
        intentFilter.addAction(ACTION_GETUSER_SUCCESS);
        getActivity().registerReceiver(brcResponse, intentFilter);

    }

    public void init() {

        userPresenter = new UserPresenter(getActivity(), this);
        userID = userPresenter.getSessionID();

        socket = getIOSocket(getActivity());

        Log.d(TAG, "init: " + socket.connected());
        socket.emit("reqtimelineget", userID);

        socket.on("restimelineget", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "call: " + args[0]);
                responseProcess(args);
            }
        });

        socket.on("restimelineadd", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "call: resssssssssssss");
                responsePostProcess(args);
            }
        });


        linearLayout = (LinearLayout) view.findViewById(R.id.layoutData);
//        linearLayout.addView(viewPost, 0);
        loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
        loadingView.start();
        viewPost = view.findViewById(R.id.layoutPost);

        lstTimeline = (RecyclerView) view.findViewById(R.id.lstTimeline);
//        lstTimeline.setLinearLayout();
//        lstTimeline.setFooterViewText("Loading...\n");

//        lstTimeline.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
//            @Override
//            public void onRefresh() {
//                YoYo.with(Techniques.SlideInDown)
//                        .duration(700)
//                        .playOn(view.findViewById(R.id.layoutPost));
//                CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        lstTimeline.setPullLoadMoreCompleted();
//                    }
//                };
//                countDownTimer.start();
//            }

//            @Override
//            public void onLoadMore() {
//                CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        lstTimeline.setPullLoadMoreCompleted();
//                    }
//                };
//                countDownTimer.start();
//            }
//        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(false);
        lstTimeline.setLayoutManager(linearLayoutManager);

        btnPost = (ActionProcessButton) view.findViewById(R.id.btnPost);
        btnPost.setMode(ActionProcessButton.Mode.ENDLESS);
        edtTimeline = (EditText) view.findViewById(R.id.edtTimeline);

        btnAddPicture = (ImageButton) view.findViewById(R.id.btnAddImage);
        imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIMG = new Intent(Intent.ACTION_PICK);
                pickIMG.setType("image/*");
                startActivityForResult(pickIMG, 6789);
            }
        });
        Picasso.with(getActivity()).load(userPresenter.getSession().getAvatar())
                .error(R.drawable.anhdaidien)
                .placeholder(R.drawable.anhdaidien)
                .into(imgAvatar);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTimeline.setEnabled(false);
                final int mProgress = 0;
                task = new Runnable() {
                    @Override
                    public void run() {
                        // do something
                        btnPost.setProgress(mProgress + 10);
                        handler.postDelayed(this, 1000);

                    }

                };
                task.run();

                final JSONObject object = new JSONObject();
                try {
                    object.put("userID", userID);
                    object.put("text", edtTimeline.getText().toString());
                    object.put("time", new Date().toString());
                    Log.d(TAG, "onClick: posttttttttttttttt");
                    if (bitmap != null) {
                        Log.d(TAG, "onClick: posttttttttttttttt");
                        HashMap dataPost = new HashMap();
                        dataPost.put("action", "upload-image");
                        dataPost.put("image", getImageBase64(bitmap));
                        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(new AsyncResponse() {
                            @Override
                            public void processFinish(String var1) {
                                try {
                                    JSONObject jsonObject = new JSONObject(var1);
                                    Log.d(TAG, "processFinish: " + jsonObject.getString("image_url"));
                                    object.put("data", jsonObject.getString("image_url"));
                                    socket.emit("reqtimelineadd", object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, dataPost);
                        postResponseAsyncTask.execute("http://trongdat.info/images/upload.php");
                    } else {
                        object.put("data", "null");
                        socket.emit("reqtimelineadd", object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_blue_bright));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                socket.emit("reqtimelineget", userID);
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
        list = new ArrayList<TimelineRow>();
//        templist = new HashSet<TimelineRow>();

        //Create the Timeline Adapter
        myAdapter = new TimelineViewAdapter(getActivity(), 0, list,
                //if true, list will be arranged by date
                true) {
            @Override
            public void loadMore() {
                Toast.makeText(getActivity(), "Loading ...", Toast.LENGTH_SHORT).show();
            }
        };


        //Get the ListView and Bind it with the Timeline Adapter
        lstTimeline.setAdapter(myAdapter);

        lstTimeline.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    if (!showPost) {
                        viewPost.setVisibility(View.VISIBLE);
                        viewPost.animate().setDuration(700).translationY(0).setListener(new AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                showPost = true;
                            }
                        });
                    }
                }
                if (dy > 2) {
                    if (showPost) {
                        viewPost.setVisibility(View.GONE);
                        showPost = false;
                    }
                }
                Log.d(TAG, "onScrolled: " + dy + "    " + dx);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6789 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            InputStream is = null;
            try {
                is = getActivity().getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(is);
            bitmap = bitmapResize(bitmap, 500, 500);
            btnAddPicture.setImageBitmap(bitmap);

        }
    }

    private void responsePostProcess(final Object... args) {
        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.removeCallbacks(task);
                        edtTimeline.setEnabled(true);
                        edtTimeline.setText(null);
                        btnPost.setProgress(0);
                        btnAddPicture.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                        JSONArray jsonArray = new JSONArray(args[0].toString());
                        JSONObject objTimeline = jsonArray.getJSONObject(0);
                        Log.d("datt", "callaaaaaaaaaa: " + objTimeline);

                        TimelineRow timelineRow = new TimelineRow(
                                //Row Id
                                objTimeline.getInt("timelineID")
                                //Row Date
                                , new Date(objTimeline.getString("time"))
                                //Row Title or null
                                , "Title"
                                //Row Description or null
                                , objTimeline.getString("text")
                                //Row Image or null
                                , objTimeline.getString("data")
                                //Row bitmap Image or null
                                , userPresenter.getSession().getAvatar()
                                //Row Bellow Line Color
                                , getRandomColor()
                                //Row Bellow Line Size in dp
                                , 2
                                //Row Image Size in dp
                                , getRandomNumber(25, 40)
                                //Row image Background color or -1
                                , -1
                                //Row Background Size in dp or -1
                                , getRandomNumber(25, 40)
                        );
                        list.add(0, timelineRow);

                        myAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void responseProcess(final Object... args) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        linearLayout.removeView(loadingView);
//                    if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                        JSONArray jsonArray = new JSONArray(args[0].toString());
//                    list = new ArrayList<TimelineRow>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objTimeline = jsonArray.getJSONObject(i);
                            Log.d("datt", "callmmmmmmmmmmm1: " + objTimeline);

                            TimelineRow timelineRow = new TimelineRow(
                                    //Row Id
                                    objTimeline.getInt("timelineID")
                                    //Row Date
                                    , new Date(objTimeline.getString("time"))
                                    //Row Title or null
                                    , "Title"
                                    //Row Description or null
                                    , objTimeline.getString("text")
                                    //Row Image or null
                                    , objTimeline.getString("data")
                                    //Row bitmap Image or null
                                    , userPresenter.getSession().getAvatar()
                                    //Row Bellow Line Color
                                    , getRandomColor()
                                    //Row Bellow Line Size in dp
                                    , 2
                                    //Row Image Size in dp
                                    , getRandomNumber(25, 40)
                                    //Row image Background color or -1
                                    , -1
                                    //Row Background Size in dp or -1
                                    , getRandomNumber(25, 40)
                            );

                            list.add(0, timelineRow);
                            myAdapter.notifyDataSetChanged();
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

    public void receiveProcess(Context context, Intent intent) {
        if (intent.getAction() == ACTION_GETUSER_SUCCESS) {
            user = (User) intent.getSerializableExtra("user");
            if (user != null) {
//                socket.emit("reqtimelineget", userID);
//            Toast.makeText(context, "" + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void scrollCompleted() {

    }

    @Override
    public void onResume() {
        super.onResume();
//        Picasso.with(getActivity()).load(userPresenter.getSessionAvatar())
//                .error(R.drawable.anhdaidien)
//                .placeholder(R.drawable.anhdaidien)
//                .into(imgAvatar);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            getActivity().unregisterReceiver(brcResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
