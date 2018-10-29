package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.Manifest;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import project14_1.cookandroid.com.mobilewhiteboard.ColorPaletteDialog;
import project14_1.cookandroid.com.mobilewhiteboard.OnColorSelectedListener;
import project14_1.cookandroid.com.mobilewhiteboard.R;
import project14_1.cookandroid.com.mobilewhiteboard.TeamChoiceActivity;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {
    View drawTool, baseView;
    ImageButton ibPicture, ibDrawing, ibClear, ibEraser, ibPen, ibSave, ibPostit, ibTxt;
    LinearLayout menuTollbar;

    //Drawing drawing;
    //슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;
    //슬라이드 레이아웃
    LinearLayout slidingMenu;

    LinearLayout slidingColor;
    ImageButton ibSliding;
    ImageButton ibSliding2;
    ImageButton ib_red;

    static int type = 0;
    static Uri rsrc = null;
    static Bitmap bitmap = null;
    static String absolutePath = null, data = null;
    protected final int GALLERY = 1;
    protected final int CAMERA = 2;

    String cameraTempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_image.jpg";
    File imageFile = new File(cameraTempFilePath);
    Uri imageFileUri = Uri.fromFile(imageFile);

    //private String[] items = {"Black", "Red", "Blue", "Yellow", "Green"};
    int mColor = 0xff000000;

    @Override
    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);

        checkDangerousPermissions();

        //UI
        slidingMenu = (LinearLayout)findViewById(R.id.slidingMenu);
        ibSliding = (ImageButton)findViewById(R.id.ibSliding);
        ibSliding2 = (ImageButton)findViewById(R.id.ibSliding2);

        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();

        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);
        ibTxt = (ImageButton) findViewById(R.id.ibText);
        ibClear = (ImageButton)findViewById(R.id.ibClear);
        ibEraser = (ImageButton)findViewById(R.id.ibEraser);
        ibPen = (ImageButton)findViewById(R.id.ibPen);
        ibSave = (ImageButton)findViewById(R.id.ibSave);
        baseView = findViewById(R.id.baseView);
        ibPostit = (ImageButton) findViewById(R.id.ibPostit);
        ibPicture = (ImageButton) findViewById(R.id.ibPicture);
        ibDrawing = (ImageButton) findViewById(R.id.ibDrawing);
        ibClear = (ImageButton)findViewById(R.id.ibClear);
        ibEraser = (ImageButton)findViewById(R.id.ibEraser);
        ibPen = (ImageButton)findViewById(R.id.ibPen);
        drawTool = findViewById(R.id.DrawTool);

        ibTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    ibSliding2.setVisibility(View.INVISIBLE);
                    ibSliding.setVisibility(View.VISIBLE);


                    isPageOpen = false;
                }
                drawTool.setVisibility(View.INVISIBLE);
                type = 1;

                AlertDialog.Builder alert = new AlertDialog.Builder(WhiteboardActivity.this);
                alert.setTitle("텍스트 입력");
                // Create TextView
                final EditText input = new EditText(WhiteboardActivity.this);
                alert.setView(input);

                alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        data = input.getText().toString();
                    }
                });

                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
            }
        });

        ibDrawing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    ibSliding2.setVisibility(View.INVISIBLE);
                    ibSliding.setVisibility(View.VISIBLE);


                    isPageOpen = false;
                }

                if(type == 2) {
                    drawTool.setVisibility(View.INVISIBLE);
                    type = 0;
                }else {
                    drawTool.setVisibility(View.VISIBLE);
                    type = 2;
                }
            }
        });

        ibPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    ibSliding2.setVisibility(View.INVISIBLE);
                    ibSliding.setVisibility(View.VISIBLE);


                    isPageOpen = false;
                }
                drawTool.setVisibility(View.INVISIBLE);
                type = 3;
                AlertDialog.Builder dlg = new AlertDialog.Builder(WhiteboardActivity.this);
                dlg.setTitle("사진 업로드 옵션");
                dlg.setMessage("사진을 불러올 앱을 선택해주세요.");
                dlg.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String strNow = sdfNow.format(date);
                        cameraTempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wb/" + strNow + ".jpg";
                        imageFile = new File(cameraTempFilePath);
                        imageFileUri = Uri.fromFile(imageFile);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                        startActivityForResult(intent, CAMERA);
                    }
                });
                dlg.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY);
                    }
                });
                dlg.show();
            }
        });

        ibPostit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    ibSliding2.setVisibility(View.INVISIBLE);
                    ibSliding.setVisibility(View.VISIBLE);

                    isPageOpen = false;
                }
                type = 4;
                drawTool.setVisibility(View.INVISIBLE);

            }
        });

        ibClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawing.canvasBitmap.eraseColor(Color.TRANSPARENT);
                Drawing.drawPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                view.invalidate();
            }
        });

        ibEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawing.drawPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                Drawing.drawPaint2.setStrokeWidth(150);
            }
        });

        ibPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPaletteDialog.listener = new OnColorSelectedListener() {
                    public void onColorSelected(int color) {
                        Drawing.drawPaint2.setXfermode(null);
                        Drawing.drawPaint2.setStrokeWidth(30);
                        ibPen.setColorFilter(color);
                        mColor = color;
                        Drawing.drawPaint2.setColor(mColor);
                    }
                };
                Intent intent = new Intent(getApplicationContext(), ColorPaletteDialog.class);
                startActivity(intent);
            }
        });

        ibSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.ibSave)
                {
                    View drawing = (View) findViewById(R.id.Drawing);
                    drawing.buildDrawingCache();
                    Bitmap captureView = drawing.getDrawingCache();
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/capture2.png");
                        captureView.compress(Bitmap.CompressFormat.PNG,100, fos);
                        Toast.makeText(getApplicationContext(), "Image Captured!", Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //버튼
    public void onButton1Clicked(View v){
        //닫기
        if(isPageOpen){
            //애니메이션 시작
            slidingMenu.startAnimation(translateRightAnim);
            ibSliding2.setVisibility(View.INVISIBLE);
            ibSliding.setVisibility(View.VISIBLE);
        }
        //열기
        else{
            slidingMenu.setVisibility(View.VISIBLE);
            slidingMenu.startAnimation(translateLeftAnim);
            ibSliding.setVisibility(View.INVISIBLE);
            ibSliding2.setVisibility(View.VISIBLE);
        }
    }

    //애니메이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if(isPageOpen){
                slidingMenu.setVisibility(View.INVISIBLE);

                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else{

                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {

        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY) {
                if (resultCode == RESULT_OK) {
                    rsrc = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), rsrc);
                    absolutePath = getAbsolutePath(rsrc);
                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    type = 0;
                }
            }else if (requestCode == CAMERA) {
                if (resultCode == RESULT_OK) {
                    rsrc = imageFileUri;
                    bitmap = BitmapFactory.decodeFile(cameraTempFilePath);
                    absolutePath = cameraTempFilePath;
                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    type = 0;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "오류 발생", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}