package utils;

import android.util.Log;

import xmu.iotlab.PWCD_1.BuildConfig;

public class LogUtils {
    private static final String TAG = "YCL";

    public static boolean debug = BuildConfig.DEBUG;

    /**
     * @ Title: showLog
     * @ Description:
     * @ param:
     * @ return:
     * @ author: Ye
     * @ date:
     */
    public static void showLog(String tag, String content) {
        if (debug)
            Log.w(tag, content);
    }
    public static void showLog(String content) {
        if (debug)
            Log.w(TAG, content);
    }
}
