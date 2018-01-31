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

public class Drawing extends View {
    int startX = -1, startY = -1, stopX = -1, stopY = -1;

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
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
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (WhiteboardActivity.type) {
            case 1:
            case 2:
            case 3:
                Rect setXY = null;
                if (startX > stopX && startY > stopY) {
                    setXY = new Rect(stopX, stopY, startX, startY);
                }else if (startX > stopX) {
                    setXY = new Rect(stopX, startY, startX, stopY);
                }else if (startY > stopY) {
                    setXY = new Rect(startX, stopY, stopX, startY);
                }else {
                    setXY = new Rect(startX, startY, stopX, stopY);
                }

                Bitmap bitmap = WhiteboardActivity.bitmap;
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                canvas.drawBitmap(scaled, null, setXY, null);
                scaled.recycle();
            case 4:
        }
    }
}