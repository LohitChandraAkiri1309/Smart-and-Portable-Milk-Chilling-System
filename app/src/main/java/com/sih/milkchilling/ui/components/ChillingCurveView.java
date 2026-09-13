package com.sih.milkchilling.ui.components;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.sih.milkchilling.data.local.ReadingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChillingCurveView extends View {

    private List<ReadingEntity> readings = new ArrayList<>();
    private double targetTempC = 4.0;
    private double warningTempC = 8.0;

    private Paint linePaint;
    private Paint fillPaint;
    private Paint targetPaint;
    private Paint warningPaint;
    private Paint pointPaint;
    private Paint textPaint;

    public ChillingCurveView(Context context) {
        super(context);
        init();
    }

    public ChillingCurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChillingCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#00E5FF"));
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setColor(Color.parseColor("#00E676"));
        targetPaint.setStrokeWidth(4f);
        targetPaint.setStyle(Paint.Style.STROKE);

        warningPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        warningPaint.setColor(Color.parseColor("#FFB300"));
        warningPaint.setStrokeWidth(3f);
        warningPaint.setStyle(Paint.Style.STROKE);
        warningPaint.setPathEffect(new DashPathEffect(new float[]{15f, 15f}, 0f));

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#00E5FF"));
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#8D99AE"));
        textPaint.setTextSize(32f);
    }

    public void setReadings(List<ReadingEntity> readings) {
        this.readings = new ArrayList<>(readings);
        Collections.sort(this.readings, Comparator.comparingLong(r -> r.timestamp));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (readings == null || readings.isEmpty()) {
            canvas.drawText("No telemetry readings available for batch", width / 4f, height / 2f, textPaint);
            return;
        }

        double minTemp = 0.0;
        double maxTemp = 40.0;

        // Draw Warning Threshold Line (8.0°C)
        float warningY = (float) (height - ((warningTempC - minTemp) / (maxTemp - minTemp) * height));
        canvas.drawLine(0, warningY, width, warningY, warningPaint);

        // Draw Target Threshold Line (4.0°C)
        float targetY = (float) (height - ((targetTempC - minTemp) / (maxTemp - minTemp) * height));
        canvas.drawLine(0, targetY, width, targetY, targetPaint);

        // Compute Points
        List<PointF> points = new ArrayList<>();
        int count = readings.size();
        for (int i = 0; i < count; i++) {
            float x = (count == 1) ? width / 2f : ((float) i / (count - 1)) * width;
            double temp = Math.max(minTemp, Math.min(maxTemp, readings.get(i).temperatureC));
            float y = (float) (height - ((temp - minTemp) / (maxTemp - minTemp) * height));
            points.add(new PointF(x, y));
        }

        // Build Paths
        Path strokePath = new Path();
        Path fillPath = new Path();

        strokePath.moveTo(points.get(0).x, points.get(0).y);
        fillPath.moveTo(points.get(0).x, height);
        fillPath.lineTo(points.get(0).x, points.get(0).y);

        for (int i = 1; i < points.size(); i++) {
            PointF p1 = points.get(i - 1);
            PointF p2 = points.get(i);
            PointF controlPoint1 = new PointF((p1.x + p2.x) / 2f, p1.y);
            PointF controlPoint2 = new PointF((p1.x + p2.x) / 2f, p2.y);
            strokePath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y);
            fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y);
        }

        fillPath.lineTo(points.get(points.size() - 1).x, height);
        fillPath.close();

        // Draw Gradient Fill
        LinearGradient gradient = new LinearGradient(
            0, 0, 0, height,
            Color.parseColor("#5500E5FF"), Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        );
        fillPaint.setShader(gradient);
        canvas.drawPath(fillPath, fillPaint);

        // Draw Stroke Line
        canvas.drawPath(strokePath, linePaint);

        // Draw Points
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, 8f, pointPaint);
        }
    }
}
