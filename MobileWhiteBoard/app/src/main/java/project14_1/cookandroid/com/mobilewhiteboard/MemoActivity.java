package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.userID;
import static project14_1.cookandroid.com.mobilewhiteboard.MemoListActivity.type;

/**
 * Created by owner on 2018-05-18.
 */

public class MemoActivity extends AppCompatActivity {

    EditText edtTitle, edtText, edtNo;
    RelativeLayout btnSave;
    String link, title, text, no;
    Toolbar toolbar;

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_memo);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("메모 등록");
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

        Intent intent = getIntent();
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtText = (EditText) findViewById(R.id.edtText);
        edtNo = (EditText) findViewById(R.id.edtNo);
        btnSave = (RelativeLayout) findViewById(R.id.btnSave);

        if(type == 1) {
            edtText.setText(intent.getStringExtra("text"));
            edtTitle.setText(intent.getStringExtra("title"));
            edtNo.setText(intent.getStringExtra("no"));

        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = edtTitle.getText().toString().trim();
                text = edtText.getText().toString().trim();
                no = edtNo.getText().toString().trim();
                //Toast.makeText(MemoActivity.this, "@@"+title+"@@@@"+text, Toast.LENGTH_SHORT).show();
                if(type == 0) {
                    insertoToDatabase(userID, title, text);
                    /*Intent intent =  new Intent(getApplication(), MemoListActivity.class);
                    startActivity(intent);*/
                    finish();
                }else if(type == 1) {
                    updateToDatabases(userID, title, text, no);
                    /*Intent intent =  new Intent(getApplication(), MemoListActivity.class);
                    startActivity(intent);*/
                    finish();
                }

            }
        });

    }

    private void insertoToDatabase(String userID, String title, String text) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MemoActivity.this, "Please Wait", null, true, true);
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
                    String title = (String) params[1];
                    String text = (String) params[2];

                    link = "http://" + MainActivity.IPaddress + "/android_db_api/memo.php";

                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
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
        task.execute(userID, title, text);
    }

    private void updateToDatabases(String userID, String title, String text, String no) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MemoActivity.this, "Please Wait", null, true, true);
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
                    String title = (String) params[1];
                    String text = (String) params[2];
                    String no = (String) params[3];

                    link = "http://" + MainActivity.IPaddress + "/android_db_api/memoUpdate.php";

                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
                    data += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");
                    data += "&" + URLEncoder.encode("no", "UTF-8") + "=" + no;

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
        task.execute(userID, title, text, no);
    }
    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(getApplication(), MemoListActivity.class);
        startActivity(intent);*/
        finish();
    }
}
