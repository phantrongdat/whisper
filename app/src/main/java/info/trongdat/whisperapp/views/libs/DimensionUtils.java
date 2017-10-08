package info.trongdat.whisperapp.views.libs;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Alone on 5/9/2017.
 */

public class DimensionUtils {
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue
                .applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        context.getResources().getDisplayMetrics()
                );
    }
}