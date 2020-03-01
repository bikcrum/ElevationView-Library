package bikcrum.elevationview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Locale;

public class Graph {
    private float maximumElevation;
    private float multiplierX;
    private int textSize = 32;
    private int zebraHeight = 15;
    private int textPadding = 10;
    private float minBarHeight = 50;
    private Context context;
    private int width;
    private int height;
    private Paint paint = new Paint();
    private boolean showStats = true;
    private float paddingStart;
    private float paddingTop;
    private float paddingEnd;
    private float paddingBottom;
    private CharSequence copyRightText;

    void setElevations(ArrayList<ArrayList<Double>> elevations) {
        this.elevations = elevations;

        maximumElevation = (float) (double) elevations.get(elevations.size() - 1).get(1);
        for (int i = elevations.size() - 2; i >= 0; i--) {
            if (elevations.get(i).get(1) > maximumElevation) {
                maximumElevation = (float) (double) elevations.get(i).get(1);
            }
        }
        multiplierX = (width - paddingStart - paddingEnd) / (float) (double) elevations.get(elevations.size() - 1).get(0);

        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    public void setShowStats(boolean showStats) {
        this.showStats = showStats;
    }

    void setPadding(float paddingStart, float paddingTop, float paddingEnd, float paddingBottom) {
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingEnd = paddingEnd;
        this.paddingStart = paddingStart;
    }

    private final static String TAG = "icols";

    private ArrayList<ArrayList<Double>> elevations;

    Graph(Context context, float paddingStart, float paddingTop, float paddingEnd, float paddingBottom) {
        this.context = context;
        this.paddingStart = paddingStart;
        this.paddingTop = paddingTop;
        this.paddingEnd = paddingEnd;
        this.paddingBottom = paddingBottom;
    }

    public Graph(Context context) {
        this.context = context;
    }

    void setWidth(int width) {
        this.width = width;
        multiplierX = (width - paddingStart - paddingEnd) / (float) (double) elevations.get(elevations.size() - 1).get(0);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCopyrightText(CharSequence copyRightText) {
        this.copyRightText = copyRightText;
    }

    void show(Canvas canvas) {
        BarPreCalculated barPreCalculated = drawBars(canvas);

        drawBarSeperators(canvas);

        drawZebra(canvas);

        drawStas(canvas, barPreCalculated);
    }

    private void drawBarSeperators(Canvas canvas) {
        float firstElevation = (float) (double) elevations.get(0).get(1);
        float lastElevation = (float) (double) elevations.get(elevations.size() - 1).get(1);
        for (int i = 1; i < elevations.size() - 1; i++) {

            ArrayList<Double> elevation = elevations.get(i);

            //draw bars
            float startX = (float) (double) (elevation.get(0) * multiplierX);
            float startY = map((float) (double) elevation.get(1), firstElevation, lastElevation, minBarHeight, height - paddingTop - paddingBottom);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);

            paint.setColor(Color.WHITE);

            Path path = new Path();
            path.moveTo(startX, 0);
            path.lineTo(startX, startY);
            path.offset(paddingStart, paddingBottom);

            canvas.save();
            canvas.scale(1, -1, width / 2, height / 2);
            canvas.drawPath(path, paint);
            canvas.restore();
        }
    }

    private void drawStas(Canvas canvas, BarPreCalculated barPreCalculated) {
        int offset = 40;
        int flagSize = 30;
        int rangeInfoGap = 20;
        int heightInfoGap = 30;

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLACK);

        ArrayList<Double> elevationFirst = elevations.get(0);
        ArrayList<Double> elevationSecond = elevations.get(1);
        ArrayList<Double> elevationLast = elevations.get(elevations.size() - 1);

        //DRAW START AND END FLAG

        float firstX = (float) (double) (elevationFirst.get(0) * multiplierX);
        float firstY = (float) (double) minBarHeight;

        float secondX = (float) (double) (elevationSecond.get(0) * multiplierX);

        float lastX = (float) (double) (elevationLast.get(0) * multiplierX);
        float lastY = (float) (double) height - paddingTop - paddingBottom;

        drawFlag(firstX, firstY, offset, flagSize, "S", Color.BLACK, canvas, false);
        drawFlag(lastX, lastY, offset, flagSize, "F", Color.BLACK, canvas, false);
        if (showStats) {
            drawFlag(barPreCalculated.maxX, barPreCalculated.maxY, offset, flagSize, String.valueOf(barPreCalculated.slope), barPreCalculated.color, canvas, true);
            drawRangeInfo(firstX, lastX, -rangeInfoGap - zebraHeight, String.format(Locale.ENGLISH, "%.1f km", elevationLast.get(0) / 1000f), canvas);
            drawRangeInfo(firstX, secondX, -rangeInfoGap * 2 - zebraHeight, String.format(Locale.ENGLISH, "%.0f m", elevationSecond.get(0)), canvas);
            drawHeightInfo(firstX - heightInfoGap, firstX, firstY, String.format(Locale.ENGLISH, "%.0f m", elevationFirst.get(1)), canvas);
            drawHeightInfo(lastX + heightInfoGap, lastX, lastY, String.format(Locale.ENGLISH, "%.0f m", elevationLast.get(1)), canvas);
            if (copyRightText != null) {
                drawCopyRightSymbol(lastX, 0, copyRightText.toString(), canvas);
            }
        }
    }

    private void drawCopyRightSymbol(float x, int y, String text, Canvas canvas) {
        canvas.save();
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.translate(paddingStart + textPadding, -paddingBottom - rect.width() + zebraHeight);
        canvas.rotate(90, x, height - y);
        canvas.drawText(text, x, height - y, paint);
        canvas.restore();
    }

    private void drawZebra(Canvas canvas) {
        for (int i = 0; i < elevations.size() - 1; i++) {

            ArrayList<Double> elevation = elevations.get(i);
            ArrayList<Double> nextElevation = elevations.get(i + 1);

            //draw bars
            float startX = (float) (double) (elevation.get(0) * multiplierX);
            float stopX = (float) (double) (nextElevation.get(0) * multiplierX);

            Path path = new Path();
            path.moveTo(startX, 0);
            path.lineTo(stopX, 0);
            path.lineTo(stopX, -zebraHeight);
            path.lineTo(startX, -zebraHeight);
            path.lineTo(startX, 0);
            path.offset(paddingStart, paddingBottom);

            paint.setStrokeWidth(2);
            paint.setStrokeCap(Paint.Cap.ROUND);

            canvas.save();
            canvas.scale(1, -1, width / 2, height / 2);
            if (i % 2 == 0) {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawPath(path, paint);
            } else {
                paint.setColor(Color.WHITE);
                canvas.drawPath(path, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawPath(path, paint);
            }
            canvas.restore();
        }
    }

    class BarPreCalculated {
        float maxX;
        float maxY;
        int color;
        int slope;
    }

    private BarPreCalculated drawBars(Canvas canvas) {
        BarPreCalculated barPreCalculated = new BarPreCalculated();

        float firstElevation = (float) (double) elevations.get(0).get(1);
        float lastElevation = (float) (double) elevations.get(elevations.size() - 1).get(1);
        double maximumSlope = Double.MIN_VALUE;
        for (int i = 0; i < elevations.size() - 1; i++) {

            ArrayList<Double> elevation = elevations.get(i);
            ArrayList<Double> nextElevation = elevations.get(i + 1);
            //draw bars

            float startX = (float) (double) (elevation.get(0) * multiplierX);
            float stopX = (float) (double) (nextElevation.get(0) * multiplierX);
            float startY = map((float) (double) elevation.get(1), firstElevation, lastElevation, minBarHeight, height - paddingTop - paddingBottom);
            float stopY = map((float) (double) nextElevation.get(1), firstElevation, lastElevation, minBarHeight, height - paddingTop - paddingBottom);

            double slope = (nextElevation.get(1) - elevation.get(1)) * 100 / (nextElevation.get(0) - elevation.get(0));

            int[] exactSlopes = {-5, 0, 10, 20};
            int[] exactSlopesColorId = {R.color.slope_minus_5, R.color.slope_0, R.color.slope_10, R.color.slope_20};

            int color = context.getResources().getColor(exactSlopesColorId[0]);
            int red = Color.red(color);
            int blue = Color.blue(color);
            int green = Color.green(color);
            int alpha = Color.alpha(color);

            for (int j = 0; j < exactSlopes.length - 1; j++) {
                if (slope > exactSlopes[j] && slope <= exactSlopes[j + 1]) {
                    double f = (slope - exactSlopes[j]) / (exactSlopes[j + 1] - exactSlopes[j]);
                    int color1 = context.getResources().getColor(exactSlopesColorId[j]);
                    int color2 = context.getResources().getColor(exactSlopesColorId[j + 1]);

                    red = getColor(f, Color.red(color1), Color.red(color2));
                    blue = getColor(f, Color.blue(color1), Color.blue(color2));
                    green = getColor(f, Color.green(color1), Color.green(color2));
                    alpha = getColor(f, Color.alpha(color1), Color.alpha(color2));
                    break;
                } else if (slope > exactSlopes[exactSlopes.length - 1]) {
                    color = context.getResources().getColor(R.color.slope_20);
                    red = Color.red(color);
                    blue = Color.blue(color);
                    green = Color.green(color);
                    alpha = Color.alpha(color);
                    break;
                }
            }

            Path path = new Path();

            path.moveTo(startX, 0);
            path.lineTo(stopX, 0);
            path.lineTo(stopX, stopY);
            path.lineTo(startX, startY);
            path.lineTo(startX, 0);

            path.offset(paddingStart, paddingBottom);

            paint.setStyle(Paint.Style.FILL);

            paint.setARGB(alpha, red, green, blue);

            if (slope > maximumSlope) {

                maximumSlope = slope;

                barPreCalculated.maxX = (startX + stopX) / 2;
                barPreCalculated.maxY = (startY + stopY) / 2;
                barPreCalculated.color = Color.argb(alpha, red, green, blue);
                barPreCalculated.slope = (int) Math.round(slope);
            }

            canvas.save();
            //flip verifically
            canvas.scale(1, -1, width / 2, height / 2);
            canvas.drawPath(path, paint);
            canvas.restore();

            //draw text
            if (showStats) {
                String text = String.valueOf(Math.round(slope));
                Rect rect = new Rect();

                for (int j = 32; j >= 0; j--) {
                    paint.setTextSize(j);
                    paint.getTextBounds(text, 0, text.length(), rect);
                    if (rect.width() < Math.abs(stopX - startX) - textPadding) {
                        break;
                    }
                }

                paint.setColor(Color.WHITE);
                canvas.drawText(text, (startX + stopX) / 2 - rect.width() / 2 + paddingStart, height - paddingBottom - textPadding, paint);
            }
        }
        return barPreCalculated;
    }

    private int getColor(double fraction, int color1, int color2) {
        return (int) ((1 - fraction) * color1 + fraction * color2);
    }

    private void drawFlag(float x, float y, float offset, float size, String text, int color, Canvas canvas, boolean isMidFlag) {
        //get proper size for flag according to text size
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        size = Math.max(size, rect.width());

        //draw flag path
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(size, 0);
        path.lineTo(size, size);
        path.lineTo(0, size);
        path.lineTo(0, 0);

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.save();
        canvas.scale(1, -1, width / 2, height / 2);
        canvas.translate(paddingStart, paddingBottom + offset);
        if (isMidFlag) {
            canvas.drawLine(x, y, x, y - offset, paint);
        } else {
            canvas.drawLine(x, y, x, -zebraHeight - offset, paint);
        }
        canvas.translate(x - size / 2, y - size / 2);
        canvas.rotate(45, size / 2, size / 2);
        canvas.drawPath(path, paint);
        canvas.restore();

        canvas.save();
        paint.setColor(Color.WHITE);
        canvas.drawText(text, x - rect.width() / 2 + paddingStart, height - y + rect.height() / 2 - paddingBottom - offset, paint);
        canvas.restore();

    }

    private void drawRangeInfo(float fromX, float toX, float y, String text, Canvas canvas) {
        //draw path for arrow start
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(12, 5);
        path.lineTo(12, -5);
        path.lineTo(0, 0);
        path.offset(fromX, y);
        canvas.save();
        canvas.translate(paddingStart, -paddingBottom);
        paint.setColor(Color.BLACK);
        canvas.scale(1, -1, width / 2, height / 2);
        canvas.drawPath(path, paint);
        canvas.drawLine(fromX, y, toX, y, paint);
        path.offset(toX - fromX, 0);
        canvas.rotate(180, toX, y);
        canvas.drawPath(path, paint);
        canvas.restore();

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.save();
        canvas.translate(-rect.width() / 2, rect.height() + textPadding);
        canvas.translate(paddingStart, -paddingBottom);
        canvas.drawText(text, (fromX + toX) / 2, height - y, paint);
        canvas.restore();
    }

    private void drawHeightInfo(float fromX, float toX, float y, String text, Canvas canvas) {
        //draw path for arrow start
        canvas.save();
        canvas.translate(paddingStart, -paddingBottom);
        paint.setColor(Color.BLACK);
        canvas.scale(1, -1, width / 2, height / 2);
        canvas.drawLine(fromX, y, toX, y, paint);
        canvas.rotate(180, toX, y);
        canvas.restore();

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.save();
        canvas.translate(paddingStart, -paddingBottom);
        if (fromX > toX) {
            canvas.translate(textPadding, rect.height() / 2);
            canvas.drawText(text, fromX, height - y, paint);
        } else {
            canvas.translate(-textPadding - rect.width(), rect.height() / 2);
            canvas.drawText(text, fromX, height - y, paint);
        }
        canvas.restore();
    }

    private float map(float value, float minValue, float maxValue, float toMapMinValue, float toMapMaxValue) {
        float slope = (toMapMaxValue - toMapMinValue) / (maxValue - minValue);
        return toMapMinValue + slope * (value - minValue);
    }
}
