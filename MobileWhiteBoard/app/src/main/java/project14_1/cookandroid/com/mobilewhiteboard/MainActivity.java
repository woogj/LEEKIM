package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private long lastTimeBackPressed;
    EditText edt_login_ID, edt_login_PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_login_ID = (EditText) findViewById(R.id.edt_login_ID);
        edt_login_PW = (EditText) findViewById(R.id.edt_login_PW);

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
                if (edt_login_ID.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (edt_login_PW.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Password를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    /*
                        if(parsedData[0][0].equals("succed")) {
                            Intent intent = new Intent(this, MainviewActivity.class);
                            startActivity(intent);
                        }else if (parsedData[0][0].equals("wrong")){
                            Toast.makeText(MainActivity.this, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }else if (parsedData[0][0].equals("non")){
                            Toast.makeText(MainActivity.this, "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "오류", Toast.LENGTH_SHORT).show();
                        }
                    */
                    Intent intent1 = new Intent(getApplication(), MainViewActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
