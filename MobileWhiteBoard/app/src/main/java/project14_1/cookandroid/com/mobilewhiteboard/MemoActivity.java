package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by com on 2018-01-21.
 */
public class MemoActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_memo_main);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainViewActivity.class);
        startActivity(intent);
    }
}
