package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import project14_1.cookandroid.com.mobilewhiteboard.R;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.userID;

/**
 * Created by com on 2018-01-21.
 */
public class MemoMainActivity extends AppCompatActivity {
    ImageButton ibMemo;
    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_memo_main);

        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
        ibMemo = (ImageButton) findViewById(R.id.ibMemo);
        ibMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplication(), MemoListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainViewActivity.class);
        startActivity(intent);
    }
}
