package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;

/**
 * Created by dlsrb on 2018-02-28.
 */

class TextHistory {
    private String text;
    private float x, y;
    private int width, height;
    private TextPaint textPaint;

    // text와 너비, 높이를 저장한다.
    public TextHistory(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.textPaint = new TextPaint();
        this.textPaint.setTextSize(60);
        this.textPaint.setColor(Color.BLACK);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        this.width = bounds.width();
        this.height = bounds.height();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void editText(String text) {
        this.text = text;
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        this.width = bounds.width();
        this.height = bounds.height();
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void drawCanvas(Canvas canvas){
        canvas.drawText(this.text, this.x, this.y, this.textPaint);
    }

    /**
     * 터치한 좌표에 텍스트바운드가 속하는지 검사
     * @param startX
     * @param startY
     * @return
     */
    public boolean isClicked(float startX, float startY) {
        Log.d("??", startX + ", " + startY + " / " + x + ", " + y + " / " + width + ", " + height);
        return startX >= x && startX <= x+width && startY >= y-height && startY <= y;
    }
}
