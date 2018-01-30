package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Drawing extends View {
    int startX = -1, startY = -1, endX = -1, endY = -1;

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
                endX = (int) event.getX();
                endY = (int) event.getY();
                this.invalidate();
                break;
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        canvas.drawLine(startX, startY, endX, endY, paint);
    }
        /*
        super.onDraw(canvas);
        //Canvas가 보유한 여러가지 기능 테스트
        //텍스트 그리기
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(80);
        canvas.drawText("텍스트입니다.", 200, 200, paint);

        //선 그리기
        Paint paint2 = new Paint();
        paint2.setColor(Color.BLACK);

        canvas.drawLine(200, 500, 500, 600, paint2);
    }
    */
}