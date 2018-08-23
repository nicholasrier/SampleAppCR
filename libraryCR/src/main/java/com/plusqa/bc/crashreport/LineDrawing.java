package com.plusqa.bc.crashreport;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

public class LineDrawing extends Drawing {

    private ArrayList<PointF> pointFs = new ArrayList<>();

    private boolean isScaled = false;

    LineDrawing(float x, float y, Paint paint) {
        super(x, y, paint);

        pointFs.add(new PointF(x, y));

        reset();
    }

    @Override
    public boolean contains(float x, float y) {

        if (isScaled) {

            return getRectF().contains(x, y);

        } else {

            RectF bounds = new RectF(x - 65, y - 65,
                    x + 65, y + 65);

            for (PointF p : pointFs) {

                if (bounds.contains(p.x, p.y)) {
                    return true;
                }

            }
        }

        return false;
    }

    @Override
    public void offsetDrawing(float offsetX, float offsetY) {

        super.offsetDrawing(offsetX, offsetY);

        offset(offsetX, offsetY);

        for (PointF p : pointFs) {
            p.set(p.x += offsetX, p.y += offsetY);
        }

    }

    @Override
    public void scaleDrawing(float scaleX, float scaleY) {

        float width = getRectF().width();
        float height = getRectF().height();
        float sx = (width + scaleX) / width;
        float sy = (height + scaleY) / height;

        super.scaleDrawing(scaleX, scaleY);

        RectF rectF = getRectF();

        Matrix scaleMatrix = new Matrix();

        scaleMatrix.setScale(sx, sy, rectF.centerX(),rectF.centerY());
        transform(scaleMatrix);

        isScaled = true;
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {

        super.quadTo(x1, y1, (x2 + x1) / 2, (y2 + y1) / 2);

        PointF p = new PointF(x2, y2); // store each point of a new line

        pointFs.add(p);

        RectF rectF = new RectF();

        computeBounds(rectF, true);

        setRectF(rectF);
    }

}
