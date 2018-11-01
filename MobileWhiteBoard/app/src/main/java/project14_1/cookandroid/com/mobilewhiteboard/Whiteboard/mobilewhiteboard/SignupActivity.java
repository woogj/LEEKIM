package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import project14_1.cookandroid.com.mobilewhiteboard.R;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.IPaddress;

/**
 * 마지막 수정 2018-01-25 이지연
 * 변수 id, pass, name, email, repass 추가
 * 토스트기능 추가
 * Created by com on 2018-01-21.
 */
public class SignupActivity extends AppCompatActivity {
    private static String TAG = "test";
    String mJsonString;
    ArrayList<HashMap<String, String>> mArrayList;

    Button btnResister, checkID;
    TextView txtLoginGo;
    EditText edtID,edtName, edtEmail, edtPw, edtRePw;
    String userID,userPW, userPWR, name, email, id, pw;

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
        checkID = (Button) findViewById(R.id.checkID);
        edtPw.setOnTouchListener(mOnTouchListener);
        edtRePw.setOnTouchListener(mOnTouchListener1);

        checkID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = edtID.getText().toString().trim();
                pw = edtPw.getText().toString().trim();
                GetData task = new GetData();
                task.execute(id,pw);
            }
        });

        btnResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userID = edtID.getText().toString().trim();
                userPW = edtPw.getText().toString().trim();
                userPWR = edtRePw.getText().toString().trim();
                name = edtName.getText().toString().trim();
                email = edtEmail.getText().toString().trim();

                if (userID.isEmpty() || name.isEmpty() || email.isEmpty() || userPW.isEmpty()|| userPWR.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        Toast.makeText(SignupActivity.this, "이메일 형식이 아닙니다.(***@**.**)", Toast.LENGTH_SHORT).show();
                    } else{
                        if (userPW.equals(userPWR)){
                            insertoToDatabase(userID, userPW, name, email);
                        }else{
                            Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인이 다르게 입력되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    }
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

    private class GetData extends AsyncTask<String, Void, String>{
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("\uFEFFWrongID")){
                Toast.makeText(SignupActivity.this, "사용 가능한 아이디 입니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignupActivity.this, "이미 사용중인 아이디 입니다.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://" + IPaddress + "/android_db_api/login.php";

            String postParameters = "userID=" + searchKeyword1 +"&userPW=" + searchKeyword2;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
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



    private void insertoToDatabase(String userID, String userPW, String name, String email) {
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
                if (s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                }else if (s.equals("failure")){
                    Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다. 아이디 중복여부를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String userID = (String) params[0];
                    String userPW = (String) params[1];
                    String name = (String) params[2];
                    String email = (String) params[3];

                    String link = "http://" + IPaddress + "/android_db_api/signup.php";
                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("userPW", "UTF-8") + "=" + URLEncoder.encode(userPW, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");


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
        task.execute(userID, userPW, name, email);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }

}
