package project14_1.cookandroid.com.mobilewhiteboard.TeamManage;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import project14_1.cookandroid.com.mobilewhiteboard.MainActivity;
import project14_1.cookandroid.com.mobilewhiteboard.MainViewActivity;
import project14_1.cookandroid.com.mobilewhiteboard.R;

public class CreateTeamActivity extends AppCompatActivity {

    String IPaddress =MainActivity.IPaddress;

    EditText edtTeamName, edtTeamTitle, edtTeamSummary;
    Button btnTeamCreate;
    String teamName, teamTitle, teamSummary;
    String masterID = MainActivity.id;
    private static String TAG = "test";
    private static final String TAG_JSON="data";
    private static final String TAG_NAME = "teamNAME";
    private static final String TAG_TITLE = "object";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_MASTER = "masterID";

    @Override
    protected void onCreate(Bundle savedIntanteState) {
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_teamcreate);



        edtTeamName = (EditText)findViewById(R.id.edtTeamName);
        edtTeamTitle = (EditText)findViewById(R.id.edtTeamTitle);
        edtTeamSummary = (EditText)findViewById(R.id.edtTeamSummary);
        btnTeamCreate= (Button)findViewById(R.id.btnTeamCreate);
        btnTeamCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                teamName = edtTeamName.getText().toString().trim();
                teamTitle = edtTeamTitle.getText().toString().trim();
                teamSummary = edtTeamSummary.getText().toString().trim();

                GetData task = new GetData();
                task.execute(teamName, teamTitle, teamSummary, masterID);
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


            if (teamName.isEmpty()){
                Toast.makeText(getApplicationContext(), " 팀 이름을 입력해주세요.", Toast.LENGTH_LONG).show();
            }else if (teamTitle.isEmpty()){
                Toast.makeText(getApplicationContext(), "주제를 입력해주세요", Toast.LENGTH_LONG).show();
            }else if(result.equals("CannotCreate")){
                Toast.makeText(getApplicationContext(), "더 이상 팀을 생성할 수 없습니다.  ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();


                Intent intent =  new Intent(getApplication(), ManageTeamActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), "팀이 생성 되었습니다.", Toast.LENGTH_LONG).show();
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String tName = (String) params[0];
            String tTitle = (String) params[1];
            String tSummary = (String) params[2];
            String mID = (String) params[3];

            String serverURL = "http://" + IPaddress + "/android_db_api/team_create.php";

            String postParameters = "teamName=" + tName +"&object=" + tTitle +"&summary=" + tSummary +"&masterID=" + mID;

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

    private void showResult() {
    }

}
