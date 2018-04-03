package project14_1.cookandroid.com.mobilewhiteboard;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by 3534aa on 2018-03-26.
 */

class PictureHistory {
    private Bitmap bitmap;
    private float x, y, width, height, gapX, gapY;
    private RectF setXY;

    public PictureHistory(Bitmap bitmap, float startX, float startY, float stopX, float stopY) {
        this.bitmap = bitmap;
        if (startX >= stopX) {
            this.x = stopX;
            this.width = startX - stopX;
        }else {
            this.x = startX;
            this.width = stopX - startX;
        }
        if (startY >= stopY) {
            this.y = stopY;
            this.height = startY - stopY;
        }else {
            this.y = startY;
            this.height = stopY - startY;
        }
        this.setXY = new RectF(this.x, this.y, this.x + this.width, this.y + this.height);
    }

    public void makeGapX (float x) {
        gapX = x - this.x;
    }

    public void makeGapY (float y) {
        gapY = y - this.y;
    }

    public void setPosition(float x, float y) {
        this.x = x - gapX;
        this.y = y - gapY;
        this.setXY = new RectF(this.x, this.y, this.x + this.width, this.y + this.height);
    }

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public RectF getSetXY() { return setXY; }

    public boolean isClicked(float startX, float startY) {
        return startX >= x && startX <= x+width && startY >= y && startY <= y+height;
    }
}
