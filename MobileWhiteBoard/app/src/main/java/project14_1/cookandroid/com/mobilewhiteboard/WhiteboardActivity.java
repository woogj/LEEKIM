package project14_1.cookandroid.com.mobilewhiteboard;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by com on 2018-01-21.
 */
public class WhiteboardActivity extends AppCompatActivity {
    ImageButton btnClear;
    ImageButton btnEraser;
    ImageButton btnPen;
    private String[] items = {"Black", "Red", "Blue", "Yellow", "Green"};
    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_whiteboard);

        ImageButton btn_drawing = (ImageButton) findViewById(R.id.btnDrawing);

        btn_drawing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                View drawing = (View) findViewById(R.id.Drawing1);
                View drawTool = (View) findViewById(R.id.DrawTool);
                drawing.setVisibility(View.VISIBLE);
                drawTool.setVisibility(View.VISIBLE);

            }
        });
        btnClear = (ImageButton)findViewById(R.id.btnClear);
        btnEraser = (ImageButton)findViewById(R.id.btnEraser);
        btnPen = (ImageButton)findViewById(R.id.btnPen);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}
