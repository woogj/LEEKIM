package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.userID;

/**
 * Created by owner on 2018-05-18.
 */

public class MemoListActivity extends AppCompatActivity {
    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_NO = "no";
    private static final String TAG_DATE = "update_date";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TEXT = "text";
    static int type = 0;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ListView lvMemo;
    ImageButton ibPlus;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);
        ibPlus = (ImageButton) findViewById(R.id.ibPlus);
        lvMemo = (ListView) findViewById(R.id.lvMemo);
        personList = new ArrayList<HashMap<String, String>>();
        getData("http://" + MainActivity.IPaddress + "/android_db_api/memoList.php"); //수정 필요

        ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 0;
                Intent intent1 = new Intent(getApplication(), MemoActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        lvMemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
    }
    protected void showList() {
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
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainViewActivity.class);
        startActivity(intent);
    }
}
