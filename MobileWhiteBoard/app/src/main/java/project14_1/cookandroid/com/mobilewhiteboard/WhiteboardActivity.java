package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.Manifest;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {
    View drawTool, baseView;
    ImageButton Ib_picture, btn_drawing, btnClear, btnEraser, btnPen, btnSave, Ib_postit, btnTxt;
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
    ImageButton btnSliding;
    ImageButton btnSliding2;
    ImageButton btn_red;


    static int type = 0;
    static Uri rsrc = null;
    static Bitmap bitmap = null;

    private String[] items = {"Black", "Red", "Blue", "Yellow", "Green"};

    @Override
    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);

        checkDangerousPermissions();

        //UI
        slidingMenu = (LinearLayout)findViewById(R.id.slidingMenu);
        btnSliding = (ImageButton)findViewById(R.id.btnSliding);
        btnSliding2 = (ImageButton)findViewById(R.id.btnSliding2);

        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();

        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);
        btnTxt = (ImageButton) findViewById(R.id.Ib_text);
        btn_drawing = (ImageButton) findViewById(R.id.btnDrawing);
        btnClear = (ImageButton)findViewById(R.id.btnClear);
        btnEraser = (ImageButton)findViewById(R.id.btnEraser);
        btnPen = (ImageButton)findViewById(R.id.btnPen);
        btnSave = (ImageButton)findViewById(R.id.btnSave);
        baseView = findViewById(R.id.baseView);
        Ib_postit = (ImageButton) findViewById(R.id.Ib_postit);
        Ib_picture = (ImageButton) findViewById(R.id.Ib_picture);
        btn_drawing = (ImageButton) findViewById(R.id.btnDrawing);
        btnClear = (ImageButton)findViewById(R.id.btnClear);
        btnEraser = (ImageButton)findViewById(R.id.btnEraser);
        btnPen = (ImageButton)findViewById(R.id.btnPen);
        drawTool = findViewById(R.id.DrawTool);
        /*ib_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "사진 선택"), 1);
                type = 3;
            }
        });
*/
        //
        btnTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    btnSliding2.setVisibility(View.INVISIBLE);
                    btnSliding.setVisibility(View.VISIBLE);


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
                        Drawing.textHistories.add(new TextHistory(input.getText().toString(), 500, 500));
                        v.invalidate();
                        //drawing.invalidate();
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

        btn_drawing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    btnSliding2.setVisibility(View.INVISIBLE);
                    btnSliding.setVisibility(View.VISIBLE);


                    isPageOpen = false;
                }
                drawTool.setVisibility(View.VISIBLE);
                type = 2;
            }
        });

        Ib_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    btnSliding2.setVisibility(View.INVISIBLE);
                    btnSliding.setVisibility(View.VISIBLE);


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
                        Toast.makeText(WhiteboardActivity.this,"아직 준비중입니다.",Toast.LENGTH_LONG).show();
                    }
                });
                dlg.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "사진 선택"), 1);
                    }
                });
                dlg.show();
                /*
                View drawing = (View) findViewById(R.id.Drawing);
                //View drawTool = (View) findViewById(R.id.DrawTool);
                drawing.setVisibility(View.VISIBLE);
                //drawTool.setVisibility(View.VISIBLE);
                type = 2;
                */
            }
        });

        Ib_postit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPageOpen){
                    slidingMenu.setVisibility(View.INVISIBLE);
                    btnSliding2.setVisibility(View.INVISIBLE);
                    btnSliding.setVisibility(View.VISIBLE);


                    isPageOpen = false;
                }
                type = 4;
                drawTool.setVisibility(View.INVISIBLE);

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawing.canvasBitmap.eraseColor(Color.TRANSPARENT);
                view.invalidate();
            }
        });
        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawing.drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                //Drawing.drawPaint.setColor(Color.WHITE);
                Drawing.drawPaint.setStrokeWidth(200);
                view.invalidate();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btnSave)
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

        btnPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Paint)Drawing.drawPaint).setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
                 AlertDialog.Builder color = new AlertDialog.Builder(WhiteboardActivity.this);
                color.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0 :
                                Drawing.drawPaint.setColor(Color.BLACK);
                                break;
                            case 1 :
                                Drawing.drawPaint.setColor(Color.RED);
                                break;
                            case 2 :
                                Drawing.drawPaint.setColor(Color.BLUE);
                                break;
                            case 3:
                                Drawing.drawPaint.setColor(Color.YELLOW);
                                break;
                            default:
                                Drawing.drawPaint.setColor(Color.GREEN);
                        }
                    }
                });
                color.create().show();
                //Drawing.drawPaint.setColor(Color.BLACK);
                Drawing.drawPaint.setStrokeWidth(20);
            }
        });


    }
    //버튼
    public void onButton1Clicked(View v){
        //닫기
        if(isPageOpen){
            //애니메이션 시작
            slidingMenu.startAnimation(translateRightAnim);
            btnSliding2.setVisibility(View.INVISIBLE);
            btnSliding.setVisibility(View.VISIBLE);
        }
        //열기
        else{
            slidingMenu.setVisibility(View.VISIBLE);
            slidingMenu.startAnimation(translateLeftAnim);
            btnSliding.setVisibility(View.INVISIBLE);
            btnSliding2.setVisibility(View.VISIBLE);
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

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
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