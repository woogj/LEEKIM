package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.Whiteboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import java.util.HashMap;

/**
 * Created by 3534aa on 2018-06-06.
 */

public class ContentHistory {
    public HashMap<String, Object> map;

    //텍스트
    public ContentHistory(String text, float x, float y) {
        map = new HashMap<String, Object>();
        map.put("type", "Text");
        map.put("text", text);
        map.put("x", x);
        map.put("y", y);
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(60);
        textPaint.setColor(Color.BLACK);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        map.put("textPaint", textPaint);
        map.put("width", (float)bounds.width());
        map.put("height", (float)bounds.height());
    }

    public void editText(String text) {
        map.put("text", text);
        Rect bounds = new Rect();
        TextPaint textPaint = (TextPaint)map.get("textPaint");
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        map.put("textPaint", textPaint);
        map.put("width", (float)bounds.width());
        map.put("height",(float)bounds.height());
    }

    public void setText(String text) {
        map.put("text", text);
    }

    public String getText() {
        return (String)map.get("text");
    }

    //그림
    public ContentHistory(String path,int color, float startX, float startY, float stopX, float stopY) {
        map = new HashMap<String, Object>();
        map.put("type", "Drawing");
        map.put("path", path);
        map.put("coord", "null");
        map.put("color", color);
        map.put("x", startX);
        map.put("width", stopX);
        map.put("y", startY);
        map.put("height", stopY);
    }

    public String getCoord() {
        return (String) map.get("coord");
    }
    public int getColor() {
        return (int) map.get("color");
    }

    public void setCoord(String coord) {
        map.put("coord", coord);
    }
    public void setColor(int color) {
        map.put("color", color);
    }

    //사진
    public ContentHistory(String imgPath, Bitmap bitmap, float startX, float startY, float stopX, float stopY) {
        map = new HashMap<String, Object>();
        map.put("type", "Picture");
        map.put("path", imgPath);
        map.put("bitmap", bitmap);
        if (startX >= stopX) {
            map.put("x", stopX);
            map.put("width", startX - stopX);
        }else {
            map.put("x", startX);
            map.put("width", stopX - startX);
        }
        if (startY >= stopY) {
            map.put("y", stopY);
            map.put("height", startY - stopY);
        }else {
            map.put("y", startY);
            map.put("height", stopY - startY);
        }

        map.put("setXY", new RectF((float)map.get("x"), (float)map.get("y"), (float)map.get("x") + (float)map.get("width"), (float)map.get("y") + (float)map.get("height")));
    }

    public ContentHistory(String imgPath, String startX, String startY, String width, String height) {
        map = new HashMap<String, Object>();
        map.put("type", "Picture");
        map.put("path", imgPath);
        map.put("bitmap", "null");
        map.put("x", Float.parseFloat(startX));
        map.put("y", Float.parseFloat(startY));
        map.put("width", Float.parseFloat(width));
        map.put("height", Float.parseFloat(height));
        map.put("setXY", new RectF((float)map.get("x"), (float)map.get("y"), (float)map.get("x") + (float)map.get("width"), (float)map.get("y") + (float)map.get("height")));
    }

    public void makeGapX (float x) {
        map.put("gapX", x - (float)map.get("x"));
    }

    public void makeGapY (float y) {
        map.put("gapY", y - (float)map.get("y"));
    }

    public void setBitmap (Bitmap bitmap) {
        map.put("bitmap", bitmap);
    }

    public Bitmap getBitmap() {
        return (Bitmap) map.get("bitmap");
    }

    public String checkBitmap() {
        return map.get("bitmap").toString();
    }

    public RectF getSetXY() {
        return (RectF)map.get("setXY");
    }

    //공용
    public void setPosition(float x, float y) {
        map.put("x", x - (float)map.get("gapX"));
        map.put("y", y - (float)map.get("gapY"));

        if (map.get("type").equals("Picture")) {
            map.put("setXY", new RectF((float)map.get("x"), (float)map.get("y"), (float)map.get("x") + (float)map.get("width"), (float)map.get("y") + (float)map.get("height")));
        }
    }

    public void drawCanvas(Canvas canvas){
        if (map.get("type").equals("Text")) {
            canvas.drawText((String) map.get("text"), (float) map.get("x"), (float) map.get("y"), (TextPaint) map.get("textPaint"));
        }else if (map.get("type").equals("Drawing")) {

        }else{}
    }

    public String getPath() {
        return (String)map.get("path");
    }
    public float getX() { return (float)map.get("x"); }
    public float getY() { return (float)map.get("y"); }
    public float getWidth() { return (float)map.get("width"); }
    public float getHeight() { return (float)map.get("height"); }

    public void setX (float x) {
        map.put("x", x);
    }
    public void setY (float y) {
        map.put("y", y);
    }
    public void setWidth (float width) {
        map.put("width", width);
    }
    public void setHeight (float height) {
        map.put("height", height);
    }

    public boolean isClicked(float startX, float startY) {
        return startX >= (float)map.get("x") && startX <= (float)map.get("x") + (float)map.get("width") && startY >= (float)map.get("y") && startY <= (float)map.get("y") + (float)map.get("height");
    }

    public String getType() {
        return (String)map.get("type");
    }
}