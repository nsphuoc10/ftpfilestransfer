package com.phuocnguyen.filestransferftp.utils;

/**
 * @author phuocnguyen
 *         Created by phuocnguyen on 12/17/2015.
 */
public class LoggerUtil {
    private static final boolean isPrintLog = true;
    public static final String appName = "FilesFTPUtils";

    public static void i(String className, String methodName, String... message) {
        if (isPrintLog) {
            android.util.Log.i(appName, "INFO[ CLASS: " + className + ". METHOD: " + methodName + ". MESSAGE: " + (message.length > 0 ? message[0] : "") + " ]");
        }
    }

    public static void e(String className, String methodName, Exception e) {
        if (isPrintLog) {
            android.util.Log.e(appName, "INFO[ CLASS: " + className + ". METHOD: " + methodName + "]", e);
        }
    }

    public static void e(String className, String methodName, String msg) {
        if (isPrintLog) {
            android.util.Log.e(appName, "INFO[ CLASS: " + className + ". METHOD: " + methodName + ". MESSAGE: " + msg + "]");
        }
    }

    public static void w(String className, String methodName, String... message) {
        if (isPrintLog) {
            android.util.Log.w(appName, "INFO[ CLASS: " + className + ". METHOD: " + methodName + ". MESSAGE: " + (message.length > 0 ? message[0] : "") + "]");
        }
    }

    public static void d(String className, String methodName, String... message) {
        if (isPrintLog) {
            android.util.Log.d(appName, "INFO[ CLASS: " + className + ". METHOD: " + methodName + ". MESSAGE: " + (message.length > 0 ? message[0] : "") + "]");
        }
    }
}
