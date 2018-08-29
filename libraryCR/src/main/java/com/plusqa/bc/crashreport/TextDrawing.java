package com.plusqa.bc.crashreport;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class TextDrawing extends Drawing {

    private StaticLayout layout;
    private String text;
    private float x, y;
    private Bitmap bitmap;


    TextDrawing(float x, float y, Paint paint) {
        super(x, y, paint);
        this.x = x;
        this.y = y;
        text = " ";
    }

    @Override
    public boolean contains(float x, float y) {


        return super.getRectF().contains(x, y);


    }

    @Override
    public void draw(Canvas canvas) {

        layout = new StaticLayout(text, new TextPaint(getPaint()), canvas.getWidth(),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        if (text != null) {
            canvas.save();
            canvas.translate(x, y);
            layout.draw(canvas);
            canvas.restore();
//            canvas.drawText(text, x, y, getPaint());
        }


    }

    public String getText() {

        if (text == null) {
            text = " ";
        }

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public float getY() {
        return y;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
