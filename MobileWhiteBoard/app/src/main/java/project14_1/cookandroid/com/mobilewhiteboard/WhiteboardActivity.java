package project14_1.cookandroid.com.mobilewhiteboard;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {

    ImageButton ib_image, btn_drawing, btnClear, btnEraser, btnPen;
    static int type = 0;
    static Uri rsrc = null;
    static Bitmap bitmap = null;

    private String[] items = {"Black", "Red", "Blue", "Yellow", "Green"};

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);

        ib_image = (ImageButton) findViewById(R.id.ib_image);
        btn_drawing = (ImageButton) findViewById(R.id.btnDrawing);
        btnClear = (ImageButton)findViewById(R.id.btnClear);
        btnEraser = (ImageButton)findViewById(R.id.btnEraser);
        btnPen = (ImageButton)findViewById(R.id.btnPen);

        ib_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "사진 선택"), 1);
                type = 3;
            }
        });

        btn_drawing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                View drawing = (View) findViewById(R.id.Drawing);
                View drawTool = (View) findViewById(R.id.DrawTool);
                drawing.setVisibility(View.VISIBLE);
                drawTool.setVisibility(View.VISIBLE);

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