package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 마지막 수정 2018-01-25 이지연
 * 변수 id, pass, name, email, repass 추가
 * 토스트기능 추가
 * Created by com on 2018-01-21.
 */
public class SignupActivity extends AppCompatActivity {

    Button btnResister;
    TextView txtLoginGo;
    EditText edtID,edtName, edtEmail, edtPw, edtRePw;
    String id,pass, name, email, repass ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtLoginGo = (TextView) findViewById(R.id.txtLoginGo);
        btnResister = (Button) findViewById(R.id.btnResister);
        edtID = (EditText) findViewById(R.id.edtID);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPw = (EditText) findViewById(R.id.edtPw);
        edtRePw = (EditText) findViewById(R.id.edtRePw);

        btnResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = edtID.getText().toString().trim();
                pass = edtPw.getText().toString().trim();
                name = edtName.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                repass = edtRePw.getText().toString().trim();

                if (id.isEmpty() || name.isEmpty() || email.isEmpty() || repass.isEmpty()|| pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(repass)){
                        Toast.makeText(getApplicationContext(), "가입완료!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인이 다르게 입력되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getApplicationContext(), "id = " + id + "password = " + pass + "checked = " + cb_autologin.isChecked(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtLoginGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }
}

/*  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);*/