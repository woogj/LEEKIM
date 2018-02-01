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
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class Drawing extends View {
    int startX = -1, startY = -1, stopX = -1, stopY = -1;
    Rect setXY = new Rect(0, 0, 0, 0);

    private Path drawPath;
    public static Paint drawPaint, canvasPaint;
    public static int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    public static Bitmap canvasBitmap;

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (WhiteboardActivity.type) {
            case 1:
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
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        stopX = (int) event.getX();
                        stopY = (int) event.getY();
                        this.invalidate();
                        break;
                }
                break;
            case 4:
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (WhiteboardActivity.type) {
            case 1:
            case 2:
                canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
                canvas.drawPath(drawPath, drawPaint);
                break;
            case 3:
                if (startX > stopX && startY > stopY) {
                    setXY = new Rect(stopX, stopY, startX, startY);
                } else if (startX > stopX) {
                    setXY = new Rect(stopX, startY, startX, stopY);
                } else if (startY > stopY) {
                    setXY = new Rect(startX, stopY, stopX, startY);
                } else {
                    setXY = new Rect(startX, startY, stopX, stopY);
                }

                Bitmap bitmap = WhiteboardActivity.bitmap;
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                canvas.drawBitmap(scaled, null, setXY, null);
                scaled.recycle();
                break;
            case 4:
        }
    }
}