package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}