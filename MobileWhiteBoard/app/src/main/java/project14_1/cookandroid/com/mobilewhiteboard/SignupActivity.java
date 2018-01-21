package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by com on 2018-01-21.
 */
public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView txtLoginGo = (TextView) findViewById(R.id.txtLoginGo);
        txtLoginGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
}

/*  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);*/