package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.TeamManage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import project14_1.cookandroid.com.mobilewhiteboard.R;
import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.MainActivity;
import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.TeamChoiceActivity;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.IPaddress;


public class ManageTeamActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button btnCreateTeam;
    ListView listTeamInfo;
    Toolbar myToolbar;
    Spinner spinTeam;
    String[] item;
    int choseSearch =0;
    static int teamtype = 0;
    String myJSON;
    JSONArray teamInfo = null;
    ArrayList<HashMap<String, String>> teamInfoList;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "teamID";
    private static final String TAG_NAME = "teamName";
    private static final String TAG_TITLE = "object";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_MASTER = "masterID";
    private static final String TAG_DATE = "reg_date";


    @Override
    protected void onCreate(Bundle savedIntanteState) {
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_teammanage);

        teamInfoList = new ArrayList<HashMap<String, String>>();
        listTeamInfo =(ListView) findViewById(R.id.listTeamInfo);
        btnCreateTeam = (Button) findViewById(R.id.btnCreateTeam);
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        actionBar.setHomeAsUpIndicator(R.drawable.back2); //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_toolbar, null);
        spinTeam = (Spinner) findViewById(R.id.spSearchTeam);

        actionBar.setCustomView(actionbar);

        spinTeam.setOnItemSelectedListener(this);
        item = new String[]{"가입된 팀","전체 팀"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner,item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinTeam.setAdapter(adapter);

        listTeamInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                teamtype = 1;
                String str;
                String teamID, teamName, object, summary, masterID, reg_date;
                str = teamInfoList.get(position).toString();

               Intent intent1 = new Intent(getApplication(), ShowTeamInfoActivity.class);

                object = str.substring(str.indexOf("object")+7,str.indexOf("teamID")-2);
                teamID = str.substring(str.indexOf("teamID")+7,str.indexOf("reg_date")-2);
                reg_date = str.substring(str.indexOf("reg_date")+9,str.indexOf("masterID")-2);
                masterID = str.substring(str.indexOf("masterID")+9,str.indexOf("summary")-2);
                summary = str.substring(str.indexOf("summary")+8,str.indexOf("teamName")-2);
                teamName = str.substring(str.indexOf("teamName")+9,str.indexOf("}")-2);

                intent1.putExtra("teamID", teamID);
                intent1.putExtra("teamName", teamName);
                intent1.putExtra("object", object);
                intent1.putExtra("summary", summary);
                intent1.putExtra("masterID", masterID);
                intent1.putExtra("reg_date", reg_date);

                startActivity(intent1);
                finish();
            }
        });



        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(),CreateTeamActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (item[pos].toString().equals("가입된 팀")){
            teamInfoList.clear();
            choseSearch = 0;
            String serverURL = "http://" + IPaddress + "/android_db_api/team_my_search.php";
            String postParameters = "id="+ MainActivity.id;
            GetData task = new GetData();
            task.execute(serverURL,postParameters);
        }else{
            teamInfoList.clear();
            choseSearch =1;
            String serverURL = "http://" + IPaddress + "/android_db_api/team_search.php";
            String postParameters = "null";
            GetData task = new GetData();
            task.execute(serverURL,postParameters);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            teamInfo = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < teamInfo.length(); i++) {
                JSONObject c = teamInfo.getJSONObject(i);
                String teamID = c.getString(TAG_ID);
                String teamName = c.getString(TAG_NAME);
                String object = c.getString(TAG_TITLE);
                String summary = c.getString(TAG_SUMMARY);
                String masterID = c.getString(TAG_MASTER);
                String reg_date = c.getString(TAG_DATE);

                HashMap<String, String> teamInfos = new HashMap<String, String>();

                teamInfos.put(TAG_ID, teamID);
                teamInfos.put(TAG_NAME, teamName);
                teamInfos.put(TAG_TITLE, object);
                teamInfos.put(TAG_SUMMARY, summary);
                teamInfos.put(TAG_MASTER, masterID);
                teamInfos.put(TAG_DATE, reg_date);

                teamInfoList.add(teamInfos);
            }
            //notifyDataSetChanged;

            ListAdapter adapter = new SimpleAdapter(
                    this, teamInfoList, R.layout.custom_teaminfo,
                    new String[]{TAG_NAME, TAG_TITLE, TAG_MASTER},
                    new int[]{ R.id.lvtvTeamName, R.id.lvtvTitle, R.id.lvtvMaster}
            );

            //adapter.notifyDataSetChanged();

            listTeamInfo.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            myJSON = result;
            showList();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = params[1];

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
                //Log.d(TAG, "response code - " + responseStatusCode);

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
                errorString = e.toString();
                return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }

}
