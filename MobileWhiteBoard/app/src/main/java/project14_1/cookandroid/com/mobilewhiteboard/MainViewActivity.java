package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by com on 2018-01-21.
 */
public class MainViewActivity extends AppCompatActivity {

    private static final long   DURATION_TIME = 2000L;
    private long prevPressTime = 0L;

    protected void onCreate(Bundle savedIntanceState){
        super.onCreate(savedIntanceState);
        setContentView(R.layout.activity_mainview);

        Button btn_personal = (Button) findViewById(R.id.btn_personal);
        Button btn_team = (Button) findViewById(R.id.btn_team);

        btn_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplication(), MemoListActivity.class);
                startActivity(intent1);
                finish();

            }
        });

        btn_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getApplication(), TeamChoiceActivity.class);
                startActivity(intent1);
                finish();

            }
        });
    }

/*    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }*/
@Override
public void onBackPressed() {
    if(System.currentTimeMillis() - prevPressTime <= DURATION_TIME) {
        moveTaskToBack(true);
        // finish();
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }else{
        prevPressTime = System.currentTimeMillis();
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 로그아웃됩니다.", Toast.LENGTH_SHORT).show();
    }
}

}
