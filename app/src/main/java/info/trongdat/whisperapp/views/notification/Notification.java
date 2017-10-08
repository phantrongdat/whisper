package info.trongdat.whisperapp.views.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;

/**
 * Created by Alone on 3/16/2017.
 */

public class Notification extends Fragment {
    View view;

    private SlackLoadingView loadingView;
    private LinearLayout linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        init();
        return view;
    }
    public void init(){
        linearLayout = (LinearLayout) view.findViewById(R.id.layoutData);
        loadingView = (SlackLoadingView) view.findViewById(R.id.loading_view);
        loadingView.start();
    }
}
