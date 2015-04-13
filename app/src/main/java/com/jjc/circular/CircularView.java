package com.jjc.circular;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.math.BigDecimal;

/**
 * 当前环形图实现原理，先画一个完整环形，然后再画一部分环形，两者叠加，显示两种不同的配比；
 * @author ThinkPad
 *
 */
public class CircularView extends View{

    /** 当前进度 */
    private int progress;
    /** 总进度 */
    private int max;

    private Paint paint;
    private RectF oval;

    private int width;
    private int height;

    private String term = "";

    /** 半径 */
    private float radius;

    /** 画笔宽度 */
    private float paintWidth;

    /** 组合字体 */
//    private float tagTextSize;

    /** 配比字体 */
    private float progressTextSize;

//    private float tagTextSize;

    /** 期限字体 */
//    private float termTextSize;

    private double rate;

    /**
     * 设置总数
     * @param max
     */
    public void setMax(int max) {
        this.max = max;
    }

    public void setPaintWidth(float paintWidth) {
        this.paintWidth = paintWidth;
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * 设置收益率
     * @param rate
     */
    public void setRate(double rate) {
        this.rate = round(rate, 2, 4);
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        oval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = canvas.getHeight();
        width = canvas.getWidth();

        initRadius();
        initPaint();
        initDebugLine(canvas ,false);
        initBankPaint(canvas);
        initFundPaint(canvas);

        initDescrip(canvas);
    }

    /**
     * 测试文字是否居中,标尺线
     */
    private void initDebugLine(Canvas canvas ,boolean isDebug) {
        if(isDebug){
            //竖线
            paint.setColor(Color.BLACK); canvas.drawLine(width/2, 0, width/2, height, paint);
            //横线
            paint.setColor(Color.GRAY); canvas.drawLine(0, height/2, width, height/2, paint);
        }
    }

    /**
     * 比较高度和宽度，然后取短的作为圆环的半径
     */
    private void initRadius() {
        if (width > height) {
            radius = (float) (height * 0.4);
        } else {
            radius = (float) (width * 0.4);
        }
    }

    private void initPaint() {
        if(paintWidth == 0){
            paintWidth = radius/10;
        }

        paint.setAntiAlias(true);// 设置是否抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);// 帮助消除锯齿
    }

    private void initBankPaint(Canvas canvas) {
        paint.setColor(getResources().getColor(R.color.pie_progress_bank));// 设置银行理财画笔

        /** 银行理财标注 */
        // 设置样式-填充
        paint.setStyle(Paint.Style.FILL);
        // 绘制一个矩形
        canvas.drawRect(new Rect(10, height-35, 22, height-15), paint);
        paint.setTextSize((float) (radius/6.5));// 设置标注文字的大小
        canvas.drawText("银行理财", 35, height-18, paint);

        /** 银行理财的饼图  */
        paint.setStrokeWidth(paintWidth);// 设置画笔宽度
        paint.setStyle(Paint.Style.STROKE);// 设置中空的样式
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }

    private void initFundPaint(Canvas canvas) {
        paint.setColor(getResources().getColor(R.color.pie_progress_fund));// 设置货币基金画笔

        /** 货币基金标注 */
        // 设置样式-填充
        paint.setStyle(Paint.Style.FILL);
        // 绘制一个矩形
        canvas.drawRect(new Rect(10, height-65, 22, height-45), paint);
        paint.setTextSize((float) (radius/6.5));// 设置标注文字的大小
        canvas.drawText("货币基金", 35, height-48, paint);

        /** 货币基金的饼图  */
        paint.setStrokeWidth(paintWidth);// 设置画笔宽度
        paint.setStyle(Paint.Style.STROKE);// 设置中空的样式
        oval.set((width / 2 - radius), (height / 2 - radius),
                (width / 2 + radius), (height / 2 + radius));
        canvas.drawArc(oval, -90, ((float) progress / max) * 360, false, paint);// 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
    }

    private void initDescrip(Canvas canvas) {
        paint.reset();// 将画笔重置
        paint.setStrokeWidth(3);// 再次设置画笔的宽度

        progressTextSize = (float) (radius / 2);

        paint.setTextSize((float) (radius / 7));// 设置标记文字的大小
        paint.setColor(getResources().getColor(R.color.text_tag_bg));// 设置画笔颜色
        canvas.drawText("组合收益率", (float) (width / 2 - radius / 2.8),
                (float) (height / 2 - progressTextSize * 0.5), paint);

        paint.setTextSize((float) (radius / 7));// 设置期限文字的大小
        paint.setColor(getResources().getColor(R.color.text_term_bg));// 设置画笔颜色
        canvas.drawText(term, (float) (width / 2 - radius / 4.5),
                (float) (height / 2 + progressTextSize * 0.7), paint);

        /** 配比数 */
        paint.setTextSize(progressTextSize);// 设置进度文字的大小
        paint.setColor(getResources().getColor(R.color.text_progress_bg));// 设置画笔颜色

        canvas.drawText(rate+"",
                (float) (width / 2 - progressTextSize * 1.1),
                (float) (height / 2 + progressTextSize / 2.5), paint);

        paint.setTextSize((float) (radius / 5));// 设置百分号的大小
        canvas.drawText("%",
                (float) (width / 2 + progressTextSize * 0.9),
                (float) (height / 2 + progressTextSize / 2.5), paint);
    }

    /**
     * 对double数据进行取精度.
     * <p>
     * For example: <br>
     * double value = 100.345678; <br>
     * double ret = round(value,4,BigDecimal.ROUND_HALF_UP); <br>
     * ret为100.3457 <br>
     *
     * @param value
     *            double数据.
     * @param scale
     *            精度位数(保留的小数位数).
     * @param roundingMode
     *            精度取值方式.
     * @return 精度计算后的数据.
     */
    private double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }

}
