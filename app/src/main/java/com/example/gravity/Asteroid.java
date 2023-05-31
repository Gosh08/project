package com.example.gravity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Asteroid {
    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;
    private Rect boundary = null;
    public boolean isLast;
    private Bitmap mBitmap;

    public Asteroid(Bitmap bitmap) {
        mBitmap = bitmap;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        boundary = new Rect();
        isLast = false;
    }

    public Asteroid(Bitmap bitmap, int x, int y) {
        mBitmap = bitmap;
        this.x = x;
        this.y = y;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        boundary = new Rect();
        isLast = false;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, (float) x, (float) y, null);
    }

    public void moveDown(int speed) {
        y += speed;
    }

    public void draw(Canvas canvas, boolean b) {
        canvas.drawBitmap(mBitmap, (float) x, (float) y, null);
    }

    public void setPosition(int x) {
        this.x = x;
    }

    public Rect getBoundary() {
        int handicap = 20;
        boundary.left = x + handicap;
        boundary.top = y + handicap;
        boundary.right = x + width - handicap;
        boundary.bottom = y + height - handicap;
        return boundary;
    }

    public void setX(int i) {
    }
    public void setY(int i) {
    }

    public int getHeight() {
        return 0;
    }

    public void setLast(boolean b) {
    }

    public int getY() {

        return y;
    }

    public boolean isLast() {

        return false;
    }
}
