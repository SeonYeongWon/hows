package kr.hs.emirim.dnj1981.testmemo1;
//주제 나오는 화면

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    Button mButton; //Alt+Enter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(SecondActivity.this, kr.hs.emirim.dnj1981.testmemo1.MainActivity.class);
                startActivity(intent);

            }
        });
    }

}
