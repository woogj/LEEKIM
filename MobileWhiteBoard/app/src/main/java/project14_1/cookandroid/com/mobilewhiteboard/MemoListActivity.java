package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


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

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.userID;

/**
 * Created by owner on 2018-05-18.
 */

public class MemoListActivity extends AppCompatActivity {
    String myJSON;
    String link;
    String no;
    EditText edtNo;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_NO = "no";
    private static final String TAG_DATE = "update_date";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TEXT = "text";
    public static int type = 0;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ListView lvMemo;
    ImageButton ibPlus;
    Cursor cursor;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("개인 메모");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 0;
                finish();
            }
        });

        lvMemo = (ListView) findViewById(R.id.lvMemo);

        personList = new ArrayList<>();
        lvMemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = 1;
                String getNo, getTitle, getText;

                JSONObject jsonObject = null;
                try {
                    jsonObject = peoples.getJSONObject(position);
                    getNo = jsonObject.getString("no");
                    getTitle = jsonObject.getString("title");
                    getText = jsonObject.getString("text");

                    Intent intent1 = new Intent(getApplication(), MemoActivity.class);
                    intent1.putExtra("no", getNo);
                    intent1.putExtra("title", getTitle);
                    intent1.putExtra("text", getText);
                    startActivity(intent1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        /*lvMemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = 1;
                String no, str, title, text;
                str = personList.get(position).toString();
                HashMap<String, String> item = personList.get(position);
                //Toast.makeText(MemoListActivity.this, personList.get(position).toString(), Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplication(), MemoActivity.class);
                text = str.substring(str.indexOf("text")+5,str.indexOf("title")-2);
                title = str.substring(str.indexOf("title")+6, str.indexOf("no")-2);
                no = str.substring(str.indexOf("no")+3, str.indexOf("}"));
                intent1.putExtra("text", text);
                intent1.putExtra("title", title);
                intent1.putExtra("no", no);
                Toast.makeText(MemoListActivity.this, no, Toast.LENGTH_SHORT).show();
                startActivity(intent1);

            }
        });*/

        lvMemo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                String getNo = null;
                JSONObject jsonObject = null;

                try {
                    jsonObject = peoples.getJSONObject(position);
                    getNo = jsonObject.getString("no");
                    deletetoToDatabase(getNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getData("http://" + MainActivity.IPaddress + "/android_db_api/memoList.php");
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData("http://" + MainActivity.IPaddress + "/android_db_api/memoList.php"); //수정 필요
    }

    private void deletetoToDatabase(String no) {
        class deleteData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MemoListActivity.this, "Please Wait", null, true, true);
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
                    String no = (String) params[0];

                    link = "http://" + MainActivity.IPaddress + "/android_db_api/memoDelete.php";

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
    }

    protected void showList() {
        personList.clear();
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                int no = c.getInt(TAG_NO);
                String title = c.getString(TAG_TITLE);
                String text = c.getString(TAG_TEXT);
                String update_date = c.getString(TAG_DATE);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_NO, String.valueOf(no));
                persons.put(TAG_TITLE, title);
                persons.put(TAG_TEXT, text);
                persons.put(TAG_DATE, update_date);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    MemoListActivity.this, personList, R.layout.memo_list_item,
                    new String[]{TAG_NO, TAG_TITLE, TAG_TEXT, TAG_DATE},
                    new int[]{ R.id.tvNo, R.id.tvTitle, R.id.tvText, R.id.tvDate}
            );
            lvMemo.setAdapter(adapter);
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
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_memo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_addmeno:
                type = 0;
                Intent intent1 = new Intent(getApplication(), MemoActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
