package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.WhiteboardActivity;


public class MainActivity extends AppCompatActivity {
    private static final long   DURATION_TIME = 2000L;
    private long prevPressTime = 0L;

    TextView ShortCut;//SC
    EditText edtLoginID, edtLoginPW;
    Button btnSign, btnLogin;

    static String userID;

    String pw, name, mJsonString;
    public static String id,

            IPaddress = "14.63.168.206"; //내 IP

    /** 서버       : 14.63.168.206
     *  LHW 집     : 192.168.219.169:81
     *  LHW 핫스팟 : 192.168.43.242
     *  LJY 집     : 192.168.0.6*/
    private static String TAG = "test";
    private static final String TAG_JSON="data";
    private static final String TAG_NAME = "name";
    private static final String TAG_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShortCut = (TextView) findViewById(R.id.connectIC);//SC
        edtLoginID = (EditText) findViewById(R.id.edtLoginID);
        edtLoginPW = (EditText) findViewById(R.id.edtLoginPW);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        ShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SC 테스팅용 숏컷
                id = "SC";
                Intent intent = new Intent(getApplication(), WhiteboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = edtLoginID.getText().toString().trim();
                pw = edtLoginPW.getText().toString().trim();

                if (id.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (pw.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    GetData task = new GetData();
                    task.execute(id,pw);
                }
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
            if (result == null){
                Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
            }else if (result.equals("\uFEFFWrongID")){
                Toast.makeText(MainActivity.this, "존재하지 않는 회원 입니다.", Toast.LENGTH_LONG).show();
            }else if (result.equals("\uFEFFWrongPW")){
                Toast.makeText(MainActivity.this, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            }else{
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://" + IPaddress + "/android_db_api/login.php";

            /** http://ip주소:포트번호/파일경로
             * ip주소   : cmd창에서 ipconfig로 IPv4의 주소를 찾아서 넣는다.
             * 포트번호 : 기본은 80번 포트이고 안적어도 된다. 포트번호가 다를 경우 적어야한다.
             *            예시1. 80번 포트 사용시 http://ip주소/파일경로
             *            예시2. 81번 포트 사용시 http://ip주소:81/파일경로
             *            참고1. localhost 사용시 오류가 생김
             * 파일경로 : 서버 내에서 php파일이 있는 경로를 적어야한다.
             *            참고1. apache의 경우는 htdocs 이후의 경로부터 쓰면 된다.
             */
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

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                name = item.getString(TAG_NAME);
                userID = item.getString(TAG_ID);
                Toast.makeText(MainActivity.this, name + "님 환영합니다.", Toast.LENGTH_LONG).show();
                test(userID);
                Intent intent =  new Intent(getApplication(), MainViewActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    public void test(String userID) {
        this.userID = userID;
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - prevPressTime <= DURATION_TIME) {
            moveTaskToBack(true);
           // finish();
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }else{
            prevPressTime = System.currentTimeMillis();
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

