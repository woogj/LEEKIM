package project14_1.cookandroid.com.mobilewhiteboard;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {
    View drawTool, baseView;
    ImageButton ib_image, Ib_picture, btn_drawing, btnClear, btnEraser, btnPen, btnSave, Ib_postit;
    static int type = 0;
    static Uri rsrc = null;
    static Bitmap bitmap = null;

    private String[] items = {"Black", "Red", "Blue", "Yellow", "Green"};


    @Override

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);
        checkDangerousPermissions();
        btn_drawing = (ImageButton) findViewById(R.id.btnDrawing);
        btnClear = (ImageButton)findViewById(R.id.btnClear);
        btnEraser = (ImageButton)findViewById(R.id.btnEraser);
        btnPen = (ImageButton)findViewById(R.id.btnPen);
        btnSave = (ImageButton)findViewById(R.id.btnSave);
        baseView = (View)findViewById(R.id.baseView);
        Ib_postit = (ImageButton) findViewById(R.id.Ib_postit);
        Ib_picture = (ImageButton) findViewById(R.id.Ib_picture);
        btn_drawing = (ImageButton) findViewById(R.id.btnDrawing);
        btnClear = (ImageButton)findViewById(R.id.btnClear);
        btnEraser = (ImageButton)findViewById(R.id.btnEraser);
        btnPen = (ImageButton)findViewById(R.id.btnPen);
        drawTool = (View) findViewById(R.id.DrawTool);
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
        btn_drawing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawTool.setVisibility(View.VISIBLE);
                type = 2;
            }
        });

        Ib_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                type = 3;
                drawTool.setVisibility(View.GONE);
                /*
                View drawing = (View) findViewById(R.id.Drawing);
                //View drawTool = (View) findViewById(R.id.DrawTool);
                drawing.setVisibility(View.VISIBLE);
                //drawTool.setVisibility(View.VISIBLE);
                type = 2;
                */
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawing.canvasBitmap.eraseColor(Color.WHITE);
                view.invalidate();
            }
        });
        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawing.drawPaint.setColor(Color.WHITE);
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

        Ib_postit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 4;
                drawTool.setVisibility(View.GONE);
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