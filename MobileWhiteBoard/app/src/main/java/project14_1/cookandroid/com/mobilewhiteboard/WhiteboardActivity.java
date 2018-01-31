package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {

    ImageButton ib_image;
    static int type = 0;
    static Uri rsrc = null;
    static Bitmap bitmap = null;

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);

        ib_image = (ImageButton) findViewById(R.id.ib_image);

        ib_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "사진 선택"), 1);
                type = 3;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (resultCode == RESULT_OK) {
                rsrc = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), rsrc);
            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "오류 발생", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}