package info.trongdat.whisperapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;

import info.trongdat.whisperapp.utils.VerifyPermissions;
import info.trongdat.whisperapp.views.user.Login;
import me.wangyuwei.particleview.ParticleView;

import static info.trongdat.whisperapp.utils.Internet.initSocket;

public class AppIntro extends AppCompatActivity {
    ParticleView mParticleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        VerifyPermissions.verify(AppIntro.this);
        initSocket(this);
        mParticleView = (ParticleView) findViewById(R.id.particleView);
        mParticleView.startAnim();
        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                Explode explode = new Explode();
                explode.setDuration(500);
                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
//                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                Intent i2 = new Intent(AppIntro.this, Login.class);
                startActivity(i2);
            }
        });
    }
}
