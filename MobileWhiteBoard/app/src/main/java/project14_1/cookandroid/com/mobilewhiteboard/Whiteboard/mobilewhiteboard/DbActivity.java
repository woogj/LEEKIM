package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
