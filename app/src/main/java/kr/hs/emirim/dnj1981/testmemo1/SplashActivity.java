package kr.hs.emirim.dnj1981.testmemo1;
//처음 로고 나오는 화면

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(intent);
                overridePendingTransition(0,R.anim.zoom_exit);
                finish();
            }
        },2000);

    }



}
