package com.plusqa.bc.crashreport;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class TextDrawing extends Drawing {

    private String text;
    private float x, y;
    private TextPaint textPaint;
    private float height;
    private float width;

    TextDrawing(float x, float y, Paint paint) {

        super(x, y, paint);
        this.x = x;
        this.y = y;
        text = " ";

        textPaint = new TextPaint(getPaint());
    }

    @Override
    public boolean contains(float x, float y) {

        RectF bounds = new RectF(x - width/2,
                y - height/2,
                x + width/2,
                y + height/2);

        bounds.offsetTo(this.x, this.y);

        setRectF(bounds);

        return getRectF().contains(x, y);

    }

    @Override
    public void draw(Canvas canvas) {

        DynamicLayout layout = new DynamicLayout(text, textPaint, canvas.getWidth(),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);



        if (text != null) {
            canvas.save();
            canvas.translate(x, y);
            layout.draw(canvas);
            canvas.restore();

            canvas.drawPath(this, getPaint());
        }

    }

    @Override
    public void offsetDrawing(float offsetX, float offsetY) {

        super.offsetDrawing(offsetX, offsetY);

        x += offsetX;
        y += offsetY;

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

    public float getTextHeight() {
        return textPaint.getTextSize();
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
