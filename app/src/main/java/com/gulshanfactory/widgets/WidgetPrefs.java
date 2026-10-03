package com.gulshanfactory.widgets;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores per-widget login credentials (username/password) so each widget
 * instance can fetch its own live data from the server.
 */
public class WidgetPrefs {
    private static final String PREFS = "gulshan_widget_prefs";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void saveLogin(Context c, int widgetId, String username, String password) {
        prefs(c).edit()
                .putString("u_" + widgetId, username)
                .putString("p_" + widgetId, password)
                .apply();
    }

    public static String getUsername(Context c, int widgetId) {
        return prefs(c).getString("u_" + widgetId, null);
    }

    public static String getPassword(Context c, int widgetId) {
        return prefs(c).getString("p_" + widgetId, null);
    }

    public static boolean hasLogin(Context c, int widgetId) {
        return getUsername(c, widgetId) != null;
    }

    public static void clear(Context c, int widgetId) {
        prefs(c).edit()
                .remove("u_" + widgetId)
                .remove("p_" + widgetId)
                .apply();
    }
}
