package project14_1.cookandroid.com.mobilewhiteboard;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 마지막 수정 2018-01-25 이지연
 * 변수 id, pass, name, email, repass 추가
 * 토스트기능 추가
 * Created by com on 2018-01-21.
 */
public class SignupActivity extends AppCompatActivity {
    String mJsonString;
    ArrayList<HashMap<String, String>> mArrayList;

    Button btnResister;
    TextView txtLoginGo;
    EditText edtID,edtName, edtEmail, edtPw, edtRePw;
    String userID,userPW, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mArrayList = new ArrayList<>();
        txtLoginGo = (TextView) findViewById(R.id.txtLoginGo);
        btnResister = (Button) findViewById(R.id.btnResister);
        edtID = (EditText) findViewById(R.id.edtID);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPw = (EditText) findViewById(R.id.edtPw);
        edtRePw = (EditText) findViewById(R.id.edtRePw);

       /* edtPw.setOnLongClickListener(mLongClickListener);
        edtRePw.setOnLongClickListener(mLongClickListener);
*/

        edtPw.setOnTouchListener(mOnTouchListener);
        edtRePw.setOnTouchListener(mOnTouchListener1);


        btnResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userID = edtID.getText().toString().trim();
                userPW = edtPw.getText().toString().trim();
                name = edtName.getText().toString().trim();
                email = edtEmail.getText().toString().trim();

                //String userID = edtID.getText().toString();
                //String userPW = edtPw.getText().toString();

                insertoToDatabase(userID, userPW, name);
                /*if (id.isEmpty() || name.isEmpty() || email.isEmpty() || repass.isEmpty()|| pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(repass)){

                        //Toast.makeText(getApplicationContext(), "가입완료!", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(getApplication(), MainActivity.class);
                        //startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인이 다르게 입력되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getApplicationContext(), "id = " + id + "password = " + pass + "checked = " + cb_autologin.isChecked(), Toast.LENGTH_SHORT).show();
                }
                */

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

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN :

                    edtPw.setInputType(0x00000001);
                    break;

                case MotionEvent.ACTION_UP :
                    edtPw.setInputType(   0x00000081);
                    break;
            }

            return false;
        }
    };

    View.OnTouchListener mOnTouchListener1 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN :

                    edtRePw.setInputType(0x00000001);


                    break;

                case MotionEvent.ACTION_UP :
                    edtRePw.setInputType(   0x00000081);

                    break;
            }

            return false;
        }
    };

    /*public void insert(View view) {
        String userID = edtID.getText().toString();
        String userPW = edtPw.getText().toString();
        insertoToDatabase(userID, userPW);
    }
    */

    private void insertoToDatabase(String userID, String userPW, String name) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignupActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String userID = (String) params[0];
                    String userPW = (String) params[1];
                    String name = (String) params[2];

                    String link = "http://221.163.228.56/signup.php";
                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("userPW", "UTF-8") + "=" + URLEncoder.encode(userPW, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(userID, userPW, name);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }

}

/*  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);*/