package cn.homecaught.ibus_jhr_wx.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class LineView extends View {

    public static final int LINE_DASHED_STYLE = 0;
    public static final int LINE_NORMAL_STYLE = 1;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;

        invalidate();
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
        invalidate();
    }

    private int style = LINE_DASHED_STYLE;
    private float lineHeight = 480;

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, lineHeight);
        PathEffect effects = null;
        if (style == LINE_DASHED_STYLE){
            effects = new DashPathEffect(new float[]{10,10,10,10},1);
        }
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }


}