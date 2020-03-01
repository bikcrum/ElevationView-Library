package bikcrum.elevationview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

public class ElevationView extends View {

    private final static String TAG = "icols";

    private Graph graph;
    private Context context;
    private boolean landscape;

    public ElevationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs, 0);
    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ElevationView, defStyleAttr, 0);

        float graphPaddingStart = a.getDimension(R.styleable.ElevationView_graphPaddingStart, 60);
        float graphPaddingTop = a.getDimension(R.styleable.ElevationView_graphPaddingTop, 50);
        float graphPaddingEnd = a.getDimension(R.styleable.ElevationView_graphPaddingEnd, 60);
        float graphPaddingBottom = a.getDimension(R.styleable.ElevationView_graphPaddingBottom, 50);
        boolean graphShowStats = a.getBoolean(R.styleable.ElevationView_showStats, true);
        CharSequence[] distanceX = a.getTextArray(R.styleable.ElevationView_distance_x);
        CharSequence[] elevationY = a.getTextArray(R.styleable.ElevationView_elevation_y);
        CharSequence copyright = a.getText(R.styleable.ElevationView_copyright);
        landscape = a.getBoolean(R.styleable.ElevationView_landscape, false);

        ArrayList<ArrayList<Double>> profile = new ArrayList<>();

        if (distanceX != null && elevationY != null) {
            for (int i = 0; i < Math.min(distanceX.length, elevationY.length); i++) {
                ArrayList<Double> xy = new ArrayList<>();
                xy.add(Double.parseDouble(distanceX[i].toString()));
                xy.add(Double.parseDouble(elevationY[i].toString()));
                profile.add(xy);
            }
        }

        graph = new Graph(context, graphPaddingStart, graphPaddingTop, graphPaddingEnd, graphPaddingBottom);
        graph.setCopyrightText(copyright);
        graph.setElevations(profile);
        graph.setShowStats(graphShowStats);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width, height;
        if (landscape) {
            canvas.save();
            canvas.rotate(270, getWidth() / 2, getHeight() / 2);
            canvas.translate((getWidth() - getHeight()) / 2, (getHeight() - getWidth()) / 2);
            width = getHeight();
            height = getWidth();
        } else {
            width = getWidth();
            height = getHeight();
        }

        graph.setWidth(width);
        graph.setHeight(height);

        graph.show(canvas);

        super.onDraw(canvas);

        if (landscape) {
            canvas.restore();
        }
    }

    public void setShowStats(boolean showStats) {
        graph.setShowStats(showStats);
        invalidate();
    }

    public void setGraphPadding(int paddingStart, int paddingTop, int paddingEnd, int paddingBottom) {
        graph.setPadding(dpToPx(paddingStart), dpToPx(paddingTop), dpToPx(paddingEnd), dpToPx(paddingBottom));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = getWidth();
        int desiredHeight = 800;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Widget Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Widget Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    public void setElevations(ArrayList<ArrayList<Double>> profile) {
        graph.setElevations(profile);
        invalidate();
    }
}