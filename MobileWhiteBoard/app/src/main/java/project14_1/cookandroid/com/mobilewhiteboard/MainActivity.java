package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_sign = (Button) findViewById(R.id.btn_sign);
        Button btn_login = (Button) findViewById(R.id.btn_login);


        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplication(), SignupActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplication(), MainViewActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
}
