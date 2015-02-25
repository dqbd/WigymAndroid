package cz.duong.wigym;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Vytvořeno David on 16. 11. 2014.
 */
public class Utils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String clearSpaces(String str) {
        return str.trim().replaceAll("\\s+", "");
    }


}
