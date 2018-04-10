package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final long   DURATION_TIME = 2000L;
    private long prevPressTime = 0L;
    EditText edt_login_ID, edt_login_PW;
    String id, pw, result;
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
                Intent intent = new Intent(getApplication(), WhiteboardActivity.class);
                // Intent intent = new Intent(getApplication(), SignupActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = edt_login_ID.getText().toString().trim();
                pw = edt_login_PW.getText().toString().trim();

                if (id.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (pw.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent1 = new Intent(getApplication(), MainViewActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }
        });
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
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - prevPressTime <= DURATION_TIME) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }else{
            prevPressTime = System.currentTimeMillis();
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /** 집     : 192.168.219.196
     *  핫스팟 :
     *  학교   : 172.16.105.124 */
}
