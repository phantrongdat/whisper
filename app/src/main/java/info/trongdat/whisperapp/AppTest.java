package info.trongdat.whisperapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AppTest extends AppCompatActivity {
    static final String TAG = "datttttt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_test);
//        ButterKnife.inject(this);

    }


    public void start(View view) {
        startActivity(new Intent(this, AppMain.class));
    }
}
