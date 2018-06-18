package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.*;

/**
 * Created by com on 2018-01-21.
 */
public class TeamTaskActivity extends AppCompatActivity {
    String myJSON;
    ArrayList<HashMap<String, String>> personList;
    ArrayList<HashMap<String, String>> personList2;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_TEAM_ID = "teamID";
    private static final String TAG_USER_ID = "userID";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEXT = "text";

    JSONArray peoples = null;
    JSONArray peoples2 = null;
    private String TName = "  모바일 프로그래밍";
    private String TSchedule="  업무 분담";
    //String[] spnMemberName = {"김영준", "이수빈", "이지연", "이현우"};

    ArrayList<String> pvtTask0 =new ArrayList<>();
    ArrayList<String> pvtTask1 =new ArrayList<>();
    ArrayList<String> pvtTask2 =new ArrayList<>();
    ArrayList<String> pvtTask3 =new ArrayList<>();

    ListView lvTask;
    TextView tvTName, tvTSchedule;
    Spinner spnMember, spnMemberAdd;
    RadioGroup rgTaskChoice;
    Button btnAddTask, btnInputTask, btnAllTask;
    RelativeLayout rlyAllTask, rlyPrivateTask, rlyTaskAdd;
    RadioButton rbAllTask, rbPrivateTask;
    EditText edtTask;
    static int type = 0;
    String teamID, userID, user, text;

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_task);

        getData("http://172.30.1.45/android_db_api/task_userName.php");


        lvTask = (ListView) findViewById(R.id.lvTask);
        tvTName = (TextView) findViewById(R.id.tvTName);
        tvTSchedule = (TextView) findViewById(R.id.tvTSchedule);
        rgTaskChoice = (RadioGroup) findViewById(R.id.rgTaskChoice);
        rbAllTask = (RadioButton) findViewById(R.id.rbAllTask);
        rbPrivateTask = (RadioButton) findViewById(R.id.rbPrivateTask);
        rlyAllTask = (RelativeLayout) findViewById(R.id.rlyALLTask);
        rlyPrivateTask = (RelativeLayout) findViewById(R.id.rlyPrivateTask);
        rlyTaskAdd = (RelativeLayout) findViewById(R.id.rlyTaskAdd);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        btnInputTask = (Button) findViewById(R.id.btnInputTask);
        btnAllTask = (Button) findViewById(R.id.btnAllTask);
        edtTask = (EditText) findViewById(R.id.edtTask);

        rbAllTask.setOnClickListener(radioButtonClickListener);
        rbPrivateTask.setOnClickListener(radioButtonClickListener);

        tvTName.setText(TName);
        tvTSchedule.setText(TSchedule);

         spnMember = (Spinner) findViewById(R.id.spnMember);
         spnMemberAdd = (Spinner) findViewById(R.id.spnMemberAdd);
         personList = new ArrayList<HashMap<String, String>>();
        personList2 = new ArrayList<HashMap<String, String>>();

        //ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Member,android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spnMember.setAdapter(adapter);
        //spnMemberAdd.setAdapter(adapter);
        btnAllTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData("http://172.30.1.45/android_db_api/task_list.php");
            }
        });
        spnMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnMemberAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnInputTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = spnMemberAdd.getSelectedItem().toString();
                user = str.substring(str.indexOf("userID")+7,str.indexOf("teamID")-2);
                text = edtTask.getText().toString().trim();
                insertoToDatabase(user, text);
                Toast.makeText(TeamTaskActivity.this, user, Toast.LENGTH_SHORT).show();
                rlyTaskAdd.setVisibility(View.INVISIBLE);
                rlyPrivateTask.setVisibility(View.VISIBLE);

            }
        });
    }

    private void insertoToDatabase(String userID, String text) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TeamTaskActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "업무가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplication(), TeamTaskActivity.class);
                    startActivity(intent);
                }else if (s.equals("failure")){
                    Toast.makeText(getApplicationContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    //String teamID = (String) params[0];
                    String userID = (String) params[0];
                    String text = (String) params[1];

                    String link = "http://" + MainActivity.IPaddress + "/android_db_api/task_save.php";
                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

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
        task.execute(userID, text);
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);

                teamID = c.getString(TAG_TEAM_ID);
                userID = c.getString(TAG_USER_ID);
                String name = c.getString(TAG_NAME);


                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_TEAM_ID, teamID);
                persons.put(TAG_USER_ID, userID);
                persons.put(TAG_NAME, name);

                personList.add(persons);
            }


            ListAdapter adapter = new SimpleAdapter(
                    TeamTaskActivity.this, personList, R.layout.task_list_name,
                    new String[]{TAG_TEAM_ID, TAG_USER_ID, TAG_NAME},
                    new int[]{ R.id.tvTeamID, R.id.tvUserID, R.id.tvName}
            );

            spnMember.setAdapter((SpinnerAdapter) adapter);
            spnMemberAdd.setAdapter((SpinnerAdapter) adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void showList2() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);

                String text = c.getString(TAG_TEXT);
                String name = c.getString(TAG_NAME);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_TEXT, text);
                persons.put(TAG_NAME, name);

                personList2.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    TeamTaskActivity.this, personList2, R.layout.task_list_item,
                    new String[]{TAG_TEXT, TAG_NAME},
                    new int[]{ R.id.tvText, R.id.tvTaskName}
            );

            lvTask.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
                showList2();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){
        @Override
       public void onClick(View view) {

            switch (rgTaskChoice.getCheckedRadioButtonId()){
                case R.id.rbAllTask:
                    rlyPrivateTask.setVisibility(View.INVISIBLE);
                    rlyAllTask.setVisibility(View.VISIBLE);
                    break;
                case R.id.rbPrivateTask:
                    rlyAllTask.setVisibility(View.INVISIBLE);
                    rlyPrivateTask.setVisibility(View.VISIBLE);

                    btnAddTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            rlyPrivateTask.setVisibility(View.INVISIBLE);
                            rlyTaskAdd.setVisibility(View.VISIBLE);


                        }
                    });



                    break;
                default:
                    Toast.makeText(getApplicationContext(), "기능을 선택하시오", Toast.LENGTH_SHORT).show();
            }
        }
  };



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}
