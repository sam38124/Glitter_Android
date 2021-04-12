/*******************************************************************************
 * Copyright (c) 2019, Semiconductor Components Industries, LLC
 * (d/b/a ON Semiconductor). All rights reserved.
 *
 * This code is the property of ON Semiconductor and may not be redistributed
 * in any form without prior written permission from ON Semiconductor.
 * The terms of use and warranty for this code are covered by contractual
 * agreements between ON Semiconductor and the licensee.
 *
 * This is Reusable Code.
 *
 * Class Name: Log
 ******************************************************************************/

package com.onsemi.protocol.utility;

/**
 * A log wrapper for android log.
 * It allows to enable/disable specific output levels
 */

public class Log {
    static final boolean LOG_ERROR = true;
    static final boolean LOG_WARN = true;
    static final boolean LOG_DEBUG = false;
    static final boolean LOG_INFO = false;
    static final boolean LOG_VERB = false;

    public static void i(String tag, String string) {
        if (LOG_INFO) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        if (LOG_ERROR) android.util.Log.e(tag, string);
    }
    public static void d(String tag, String string) {
        if (LOG_DEBUG) android.util.Log.d(tag, string);
    }
    public static void v(String tag, String string) {
        if (LOG_VERB) android.util.Log.v(tag, string);
    }
    public static void w(String tag, String string) {
        if (LOG_WARN) android.util.Log.w(tag, string);
    }
}
