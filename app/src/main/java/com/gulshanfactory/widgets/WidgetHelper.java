package com.gulshanfactory.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Shared logic for all Gulshan Factory widgets.
 *
 * Everything is computed on-device (Asia/Karachi timezone) so the widgets
 * work without any login or network access. Tapping a widget opens the
 * matching section of the web app, where live order data is shown.
 */
public class WidgetHelper {

    public static final String APP_URL = "https://kashf.alwaysdata.net";
    public static final String URL_DAILY = APP_URL + "/#daily";
    public static final String URL_SUPPLY = APP_URL + "/#supply";

    /** Daily order cut-off time (matches the server's daily_settings default). */
    public static final int CUTOFF_HOUR = 20;
    public static final int CUTOFF_MINUTE = 0;

    /**
     * Warning window before cut-off, in minutes. The shop-reminder widget shows
     * its red warning state ONLY inside this final period (admin setting is
     * 60 minutes by default; mirrored here because widgets cannot read the
     * server setting without a login).
     */
    public static final int WARNING_MINUTES = 60;

    public static TimeZone khi() {
        return TimeZone.getTimeZone("Asia/Karachi");
    }

    /** Milliseconds from now (Karachi) until today's 20:00 cut-off. Negative if passed. */
    public static long millisToCutoff() {
        Calendar now = Calendar.getInstance(khi());
        Calendar cut = (Calendar) now.clone();
        cut.set(Calendar.HOUR_OF_DAY, CUTOFF_HOUR);
        cut.set(Calendar.MINUTE, CUTOFF_MINUTE);
        cut.set(Calendar.SECOND, 0);
        cut.set(Calendar.MILLISECOND, 0);
        return cut.getTimeInMillis() - now.getTimeInMillis();
    }

    /**
     * Production date per the daily-order rule: before today's cut-off the
     * orders are for tomorrow, after cut-off they are for the day after.
     */
    public static String productionDate() {
        Calendar now = Calendar.getInstance(khi());
        now.add(Calendar.DATE, millisToCutoff() > 0 ? 1 : 2);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now.getTime());
    }

    public static String todayKhi() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(Calendar.getInstance(khi()).getTime());
    }

    /** Human-readable countdown, e.g. "5 گھنٹے 23 منٹ باقی". */
    public static String formatCountdown(long diffMillis) {
        if (diffMillis <= 0) {
            return "کٹ آف گزر چکا";
        }
        long mins = diffMillis / 60000L;
        long h = mins / 60;
        long m = mins % 60;
        if (h > 0) {
            return h + " گھنٹے " + m + " منٹ باقی";
        }
        return m + " منٹ باقی";
    }

    /** PendingIntent that opens a URL in the browser when the widget is tapped. */
    public static PendingIntent openUrl(Context ctx, String url, int requestCode) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(ctx, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** All widget ids for a provider (used when an alarm fires without ids). */
    public static int[] allWidgetIds(Context ctx, AppWidgetManager mgr, Class<?> cls) {
        return mgr.getAppWidgetIds(new ComponentName(ctx, cls));
    }
}
