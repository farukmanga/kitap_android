package com.example.kitap;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DimensionHelper {

    private static Context context;

    private static float DP;
    private static float SP;

    public static void init(Context context) {

        DimensionHelper.context = context;

        DP = dpToPx(1);
        SP = spToPx(1);
    }

    public static float dpToPx(int px) {

        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }

    public static float spToPx(float px) {

        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    public static float getDP() {
        return DP;
    }

    public static float getSP() {
        return SP;
    }
}
