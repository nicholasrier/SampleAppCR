package com.plusqa.bc.crashreport;

import android.graphics.Paint;
import android.graphics.RectF;

public class OvalDrawing extends Drawing {

    OvalDrawing(float x, float y, Paint paint) {

        super(x, y, paint);

        RectF rectF = new RectF(x - 200,
                y - 200,
                x + 200,
                y + 200);

        setRectF(rectF);

        addOval(rectF, Direction.CW);
    }

    @Override
    public boolean contains(float x, float y) {

        RectF rectF = super.getRectF();

        float dx = x - rectF.centerX();
        float dy = y - rectF.centerY();

        float width = rectF.width()/2;
        float height = rectF.height()/2;

        return (dx * dx) / (width * width) + (dy * dy) / (height * height) <= 1;

    }


    @Override
    public void offsetDrawing(float offsetX, float offsetY) {

        super.offsetDrawing(offsetX, offsetY);

        reset();

        addOval(getRectF(), Direction.CW);

    }

    @Override
    public void scaleDrawing(float scaleX, float scaleY) {

        super.scaleDrawing(scaleX, scaleY);

        reset();

        addOval(getRectF(), Direction.CW);

    }

}
