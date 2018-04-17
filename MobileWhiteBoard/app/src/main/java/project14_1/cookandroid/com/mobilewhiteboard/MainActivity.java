package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final long   DURATION_TIME = 2000L;
    private long prevPressTime = 0L;
    EditText edt_login_ID, edt_login_PW;
    Button btn_sign, btn_login;
    String id, pw, name, mJsonString;

    private static String TAG = "test";
    private static final String TAG_JSON="data";
    private static final String TAG_NAME = "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_login_ID = (EditText) findViewById(R.id.edt_login_ID);
        edt_login_PW = (EditText) findViewById(R.id.edt_login_PW);

        btn_sign = (Button) findViewById(R.id.btn_sign);
        btn_login = (Button) findViewById(R.id.btn_login);

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
                    GetData task = new GetData();
                    task.execute(id,pw);
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

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,"Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
                Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
            }else if (result.equals("FAIL")){
                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                //FAIL의 이유 세분화해서 받아오는 것으로 바꿀 예정
            }else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];
            String serverURL = "http://192.168.43.242:81/android_db_api/Ulogin.php";
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
                Toast.makeText(MainActivity.this, name + "님 환영합니다.", Toast.LENGTH_LONG).show();
                Intent intent =  new Intent(getApplication(), MainViewActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
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

    /** LHW 집     : 192.168.219.196
     *  LHW 핫스팟 : 192.168.43.242 */
}
