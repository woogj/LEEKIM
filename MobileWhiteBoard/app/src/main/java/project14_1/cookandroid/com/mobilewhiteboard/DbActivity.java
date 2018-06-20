package project14_1.cookandroid.com.mobilewhiteboard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by owner on 2018-04-09.
 */

public class DbActivity extends AppCompatActivity {
    private final String urlPath = "http://" + MainActivity.IPaddress + "121.136.127.220/PHP_connection.php";
    private final String TAG = "PHPTEST";

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
    }

}
