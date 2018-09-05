package com.plusqa.bc.crashreport;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;

import java.util.ArrayList;

public class TextDrawing {

    private String text;
    public float x, y;
    private TextPaint textPaint;
    private float height;
    private float width;
    private float offsetX, offsetY;
    private final int moveThreshold = 20;
    private boolean deleted = false;

    private ArrayList<Adjustment> doneAdjustments = new ArrayList<>();

    private ArrayList<Adjustment> undoneAdjustments = new ArrayList<>();

    private ArrayList<String> doneStrings = new ArrayList<>();

    private ArrayList<String> undoneStrings = new ArrayList<>();

    public boolean isMoved() {
        return Math.abs(this.offsetX) > moveThreshold || Math.abs(this.offsetY) > moveThreshold;
    }

    private class Adjustment {

        float offsetX, offsetY;

        Adjustment(float offsetX, float offsetY) {

            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }


    TextDrawing(float x, float y, Paint paint) {

        this.x = x;
        this.y = y;
        text = " ";

        textPaint = new TextPaint(paint);
    }

    public boolean contains(float x, float y) {

        RectF bounds = new RectF(x - width/2,
                y - height/2,
                x + width/2,
                y + height/2);

        bounds.offsetTo(this.x, this.y);


        return bounds.contains(x, y);

    }


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

        }

    }

    public boolean offsetText(float offsetX, float offsetY) {

        this.offsetX += offsetX;
        this.offsetY += offsetY;

        if (isMoved()) {

            x += offsetX;
            y += offsetY;

            return true;
        }

        return false;

    }

    public String getText() {

        if (text == null) {
            text = " ";
        }

        return text;
    }

    public void saveAdjustment() {

        if (offsetX >= 0) {
            offsetX -= moveThreshold;
        } else {
            offsetX += moveThreshold;
        }

        if (offsetY >= 0) {
            offsetY -= moveThreshold;
        } else {
            offsetY += moveThreshold;
        }

        doneAdjustments.add(new Adjustment(offsetX, offsetY));

        undoneAdjustments.clear();

        offsetX = 0;
        offsetY = 0;

    }

    public void redoAdjust() {

        if (!undoneAdjustments.isEmpty()) {

            Adjustment ta = undoneAdjustments.get(undoneAdjustments.size() - 1);

            doneAdjustments.add(ta);

            undoneAdjustments.remove(ta);

            x += ta.offsetX;
            y += ta.offsetY;

            offsetX = 0;
            offsetY = 0;

        }
    }

    public void undoAdjust() {

        if (!doneAdjustments.isEmpty()) {

            Adjustment ta = doneAdjustments.get(doneAdjustments.size() - 1);

            undoneAdjustments.add(ta);

            doneAdjustments.remove(ta);

            x -= ta.offsetX;
            y -= ta.offsetY;

            offsetX = 0;
            offsetY = 0;

        }
    }

    public void saveChangeText() {

        doneStrings.add(text);

        undoneStrings.clear();

    }

    public void redoChangeText() {

        if (!undoneStrings.isEmpty()) {

            String s = undoneStrings.get(undoneStrings.size() - 1);

            doneStrings.add(s);

            undoneStrings.remove(s);

            if (!doneStrings.isEmpty()) {
                text = doneStrings.get(doneStrings.size() - 1);
            }

        }

    }

    public void undoChangeText() {

        if (!doneStrings.isEmpty()) {

            String s = doneStrings.get(doneStrings.size() - 1);

            undoneStrings.add(s);

            doneStrings.remove(s);

            if (!doneStrings.isEmpty()) {
                text = doneStrings.get(doneStrings.size() - 1);
            }

        }

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

    public int getColor() {
        return textPaint.getColor();
    }

    public void delete() {

        if (offsetX >= 0) {
            offsetX -= moveThreshold;
        } else {
            offsetX += moveThreshold;
        }

        if (offsetY >= 0) {
            offsetY -= moveThreshold;
        } else {
            offsetY += moveThreshold;
        }

        x -= offsetX;
        y -= offsetY;

        deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void redoDelete() {
        deleted = true;
    }


    public void undoDelete() {
        deleted = false;
    }

}
