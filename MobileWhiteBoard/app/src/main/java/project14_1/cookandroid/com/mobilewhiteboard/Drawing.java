package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Path;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class Drawing extends View {
    int startX = -1, startY = -1, stopX = -1, stopY = -1;
    int oldx = -1, oldy = -1;
    EditText edt;
    WhiteboardActivity cnxt;
    Rect setXY = new Rect(0, 0, 0, 0);
    boolean et = false; //et는 end trigger를 줄인것

    private Path drawPath;
    public static Paint drawPaint, canvasPaint;
    public static int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    public static Bitmap canvasBitmap;

    static List<Pictures> pictures = new ArrayList<Pictures>();

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        cnxt = (WhiteboardActivity) context;
        setupDrawing();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (WhiteboardActivity.type) {
            case 1:
                break;
            case 2:
                canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
                canvas.drawPath(drawPath, drawPaint);
                break;
            case 3:
                Bitmap bitmap = null;
                for (int i = 0; i < pictures.size(); i++) {
                    Pictures picture = pictures.get(i);
                    int nh = (int) (picture.bitmap.getHeight() * (1024.0 / picture.bitmap.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(picture.bitmap, 1024, nh, true);
                    canvas.drawBitmap(scaled, null, picture.setXY, null);
                    scaled.recycle();
                }
                if (et == true) {
                    WhiteboardActivity.type = 0;
                    et = false;
                    startX = 0;
                    startY = 0;
                    stopX = 0;
                    stopY = 0;
                }else {
                    if (startX > stopX && startY > stopY) {
                        setXY = new Rect(stopX, stopY, startX, startY);
                    } else if (startX > stopX) {
                        setXY = new Rect(stopX, startY, startX, stopY);
                    } else if (startY > stopY) {
                        setXY = new Rect(startX, stopY, stopX, startY);
                    } else {
                        setXY = new Rect(startX, startY, stopX, stopY);
                    }

                    bitmap = WhiteboardActivity.bitmap;
                    int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                    canvas.drawBitmap(scaled, null, setXY, null);
                    scaled.recycle();
                }
                break;
            case 4:


                if(canvasBitmap != null){
                    canvas.drawBitmap(canvasBitmap, 0, 0, null);
                }
                break;
            default:
                break;
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        switch (WhiteboardActivity.type) {
            case 1:
                break;
            case 2:
                float touchX = event.getX();
                float touchY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawPath.moveTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawPath.lineTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:
                        drawPath.lineTo(touchX, touchY);
                        drawCanvas.drawPath(drawPath, drawPaint);
                        drawPath.reset();

                        break;
                    default:
                        return false;
                }
                invalidate();
                break;
            case 3:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        et = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopX = (int) event.getX();
                        stopY = (int) event.getY();
                        et = false;
                        this.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (startX > stopX && startY > stopY) {
                            setXY = new Rect(stopX, stopY, startX, startY);
                        } else if (startX > stopX) {
                            setXY = new Rect(stopX, startY, startX, stopY);
                        } else if (startY > stopY) {
                            setXY = new Rect(startX, stopY, stopX, startY);
                        } else {
                            setXY = new Rect(startX, startY, stopX, stopY);
                        }

                        Pictures picture = new Pictures();
                        picture.setXY = setXY;
                        picture.bitmap = WhiteboardActivity.bitmap;
                        pictures.add(picture);
                        et = true;
                        this.invalidate();
                        break;
                }
                break;
            case 4:

                int X = (int) event.getX();
                int Y = (int) event.getY();

                edt = new EditText(cnxt);
                edt.setText("SUCCESS");
                edt.setBackgroundResource(R.drawable.postit2);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                edt.setLayoutParams(lp);
                ((RelativeLayout) this.getParent()).addView(edt);

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    oldx = X;
                    oldy = Y;

                    edt.setX(event.getX());
                    edt.setY(event.getY());

                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    if(oldx !=  -1){
                        invalidate();
                    }
                    oldx = -1;
                    oldy = -1;

                }
                break;
            default:
                break;
        }
        return true;
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged( w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    public void clear() {
        canvasBitmap.eraseColor(Color.WHITE);
        invalidate();
    }


    private static class Pictures {
        Rect setXY = new Rect(0, 0, 0, 0);
        Bitmap bitmap;
    }
}