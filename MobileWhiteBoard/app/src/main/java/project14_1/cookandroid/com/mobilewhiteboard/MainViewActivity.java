package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by com on 2018-01-21.
 */
public class MainViewActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedIntanceState){
        super.onCreate(savedIntanceState);
        setContentView(R.layout.activity_mainview);

        Button btn_personal = (Button) findViewById(R.id.btn_personal);
        Button btn_team = (Button) findViewById(R.id.btn_team);

        btn_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getApplication(), MemoActivity.class);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }

}
