package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    String taskJSON;
    String link;
    ArrayList<HashMap<String, String>> personList;
    ArrayList<HashMap<String, String>> personList2;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_TEAM_ID = "teamID";
    private static final String TAG_USER_ID = "userID";
    private static final String TAG_NO = "no";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEXT = "text";

    JSONArray peoples = null;
    JSONArray userName = null;
    private String TName = "  모바일 프로그래밍";
    private String TSchedule="  업무 분담";

    ListView lvTask;
    //TextView tvTName;
    //tvTSchedule, tvName, tvUserID, tvNo;
    Spinner spnMemberAdd;
    RadioGroup rgTaskChoice;
    Button  btnAllTask, btnClose;
    ImageButton btnAddTask,btnInputTask;
    RelativeLayout rlyAllTask, rlyPrivateTask, rlyALLTask;
    RelativeLayout layout_schedule,layout_private;
    CardView rlyTaskAdd;
    RadioButton rbAllTask, rbPrivateTask;
    EditText edtTask;
    String teamID, userID, user, text,no, name;
    int flag=0;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_task);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("업무 분담");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDataName("http://" + MainActivity.IPaddress + "/android_db_api/task_userName.php");
        getDataList("http://" + MainActivity.IPaddress + "/android_db_api/task_list.php");

        lvTask = (ListView) findViewById(R.id.lvTask);
        //tvTName = (TextView) findViewById(R.id.tvTName);
    /*    tvTSchedule = (TextView) findViewById(R.id.tvTSchedule);
        rgTaskChoice = (RadioGroup) findViewById(R.id.rgTaskChoice);
        rbAllTask = (RadioButton) findViewById(R.id.rbAllTask);
        rbPrivateTask = (RadioButton) findViewById(R.id.rbPrivateTask);*/
        rlyAllTask = (RelativeLayout) findViewById(R.id.rlyALLTask);
        layout_schedule = (RelativeLayout) findViewById(R.id.layout_schedule);
        layout_private = (RelativeLayout) findViewById(R.id.layout_private);
        layout_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                rlyPrivateTask.setVisibility(View.INVISIBLE);
                rlyAllTask.setVisibility(View.VISIBLE);
            }
        });
        rlyPrivateTask = (RelativeLayout) findViewById(R.id.rlyPrivateTask);
        layout_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataName("http://" + MainActivity.IPaddress + "/android_db_api/task_userName.php");
                rlyAllTask.setVisibility(View.INVISIBLE);
                rlyPrivateTask.setVisibility(View.VISIBLE);
                flag = 0;

                btnAddTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        rlyPrivateTask.setVisibility(View.INVISIBLE);
                        rlyTaskAdd.setVisibility(View.VISIBLE);


                    }
                });
            }
        });
        rlyTaskAdd = (CardView) findViewById(R.id.rlyTaskAdd);
        btnAddTask = (ImageButton) findViewById(R.id.btnAddTask);
        btnInputTask = (ImageButton) findViewById(R.id.btnInputTask);
        edtTask = (EditText) findViewById(R.id.edtTask);
        //btnClose = (Button) findViewById(R.id.btnClose);

      //  rbAllTask.setOnClickListener(radioButtonClickListener);
//        rbPrivateTask.setOnClickListener(radioButtonClickListener);

 //       tvTName.setText(TName);
//        tvTSchedule.setText(TSchedule);

        spnMemberAdd = (Spinner) findViewById(R.id.spnMemberAdd);
        personList = new ArrayList<HashMap<String, String>>();
        personList2 = new ArrayList<HashMap<String, String>>();

/*        btnAllTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataList("http://" + MainActivity.IPaddress + "/android_db_api/task_list.php");
            }
        });*/

        spnMemberAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getId = null;
                JSONObject jsonObject = null;
                try {
                    jsonObject = userName.getJSONObject(position);
                    getId = jsonObject.getString("userID");
                  //  tvUserID.setText(getId);
                    userID =getId;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnInputTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputID, inputNo;
             //   inputID = tvUserID.getText().toString().trim();
             //   inputNo = tvNo.getText().toString().trim();
                inputID= userID;
                inputNo = no;
                text = edtTask.getText().toString().trim();            
                //Toast.makeText(TeamTaskActivity.this, inputID, Toast.LENGTH_SHORT).show();
                if(flag==0) {
                    insertoToDatabase(inputID, text);
                }else {
                    updateToDatabase(inputNo, text);
                }
                rlyTaskAdd.setVisibility(View.INVISIBLE);
                //rlyALLTask.bringChildToFront(rlyTaskAdd);
                //rlyPrivateTask.setVisibility(View.VISIBLE);
                edtTask.setText("");
            }
        });
/*
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlyTaskAdd.setVisibility(View.INVISIBLE);
            }
        });
*/
        lvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rlyTaskAdd.setVisibility(View.VISIBLE);
                flag = 1;
                String getNo = null;
                String getText = null;

                JSONObject jsonObject = null;
                try {
                    jsonObject = peoples.getJSONObject(position);
                    getNo = jsonObject.getString("no");
                    getText = jsonObject.getString("text");
                    edtTask.setText(getText);
                    //tvNo.setText(getNo);
                    no = getNo;
                    Log.i("getNo", getNo + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        });
        lvTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                String str;
                String getNo = null;

                str = personList2.get(position).toString();

                JSONObject jsonObject = null;
                try {
                    jsonObject = peoples.getJSONObject(position);
                    getNo = jsonObject.getString("no");
                    deletetoToDatabase(getNo);
                    Log.i("getNo", getNo + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
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
                    getDataList("http://" + MainActivity.IPaddress + "/android_db_api/task_list.php");
                    flag = 0;
                    rlyPrivateTask.setVisibility(View.INVISIBLE);
                    rlyAllTask.setVisibility(View.VISIBLE);
                }else if (s.equals("failure")){
                    Toast.makeText(getApplicationContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
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

    private void updateToDatabase(String userID, String text) {
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
                    Toast.makeText(getApplicationContext(), "업무가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    getDataList("http://" + MainActivity.IPaddress + "/android_db_api/task_list.php");

                }else if (s.equals("failure")){
                    Toast.makeText(getApplicationContext(), "수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String userID = (String) params[0];
                    String text = (String) params[1];

                    String link = "http://" + MainActivity.IPaddress + "/android_db_api/task_update.php";
                    String data = URLEncoder.encode("no", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
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

    private void deletetoToDatabase(String no) throws JSONException {
        class deleteData extends AsyncTask<String, Void, String> {
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
                Toast.makeText(getApplicationContext(), "업무가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String no = (String) params[0];

                    link = "http://" + MainActivity.IPaddress + "/android_db_api/task_delete.php";

                    String data = URLEncoder.encode("no", "UTF-8") + "=" + URLEncoder.encode(no, "UTF-8");

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
        deleteData task = new deleteData();
        task.execute(no);
        getDataList("http://" + MainActivity.IPaddress + "/android_db_api/task_list.php");
        showList2();
    }

    protected void showList() {
        personList.clear();
        String[] getId;
        String[] getName;
        int cnt = 0;
        getId = new String[cnt];
        getName = new String[cnt];

        try {
            JSONObject jsonObj = new JSONObject(taskJSON);
            userName = jsonObj.getJSONArray(TAG_RESULTS);
            cnt = userName.length();
            getId = new String[cnt];
            getName = new String[cnt];

            for (int i = 0; i < cnt; i++) {
                JSONObject jsonObject = userName.getJSONObject(i);
                Log.i("JSON Object@@@", jsonObject + "");

                getId[i] = jsonObject.getString("userID");
                getName[i] = jsonObject.getString("name");
                Log.i("JsonParsing", getId[i] + "," + getName[i] + "," + getName[i]);

                String id = jsonObject.getString(TAG_USER_ID);
                String name = jsonObject.getString(TAG_NAME);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_USER_ID, id);
                persons.put(TAG_NAME, name);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    TeamTaskActivity.this, personList, R.layout.task_list_name,
                    new String[]{ TAG_NAME },
                    new int[]{ R.id.tvName}
            );
            spnMemberAdd.setAdapter((SpinnerAdapter) adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void showList2() throws JSONException {
        personList2.clear();
        String[] getNo;
        String[] getText;
        String[] getName;
        int cnt = 0;
        getNo = new String[cnt];
        getText = new String[cnt];
        getName = new String[cnt];

        try {
            JSONObject jsonObj = new JSONObject(taskJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            cnt = peoples.length();
            getNo = new String[cnt];
            getText = new String[cnt];
            getName = new String[cnt];
            for (int i = 0; i < cnt; i++) {
                JSONObject jsonObject = peoples.getJSONObject(i);
                Log.i("JSON Object@@@", jsonObject + "");

                getNo[i] = jsonObject.getString("no");
                getText[i] = jsonObject.getString("text");
                getName[i] = jsonObject.getString("name");
                Log.i("JsonParsing", getNo[i] + "," + getText[i] + "," + getName[i]);

                String no = jsonObject.getString(TAG_NO);
                String text = jsonObject.getString(TAG_TEXT);
                String name = jsonObject.getString(TAG_NAME);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_NO, no);
                persons.put(TAG_TEXT, text);
                persons.put(TAG_NAME, name);

                personList2.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    TeamTaskActivity.this, personList2, R.layout.task_list_item,
                    new String[]{TAG_NO, TAG_TEXT, TAG_NAME},
                    new int[]{ R.id.tvNo, R.id.tvText, R.id.tvTaskName}
            );
            lvTask.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDataList(String url) {
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
                taskJSON = result;
                //showList();
                try {
                    showList2();
                    //showList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


   /* RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){
        @Override
       public void onClick(View view) {

            switch (rgTaskChoice.getCheckedRadioButtonId()){
                case R.id.rbAllTask:

                    break;
                case R.id.rbPrivateTask:
                    getDataName("http://" + MainActivity.IPaddress + "/android_db_api/task_userName.php");
                    rlyAllTask.setVisibility(View.INVISIBLE);
                    rlyPrivateTask.setVisibility(View.VISIBLE);
                    flag = 0;

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
  };*/

    public void getDataName(String url) {
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
                taskJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}
