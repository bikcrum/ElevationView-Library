package bikcrum.elevationview;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Util {
    static int px2dp(int myPixels) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, myPixels, displaymetrics);
    }
}
