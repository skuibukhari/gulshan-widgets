package com.gulshanfactory.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * 5. Shop Order Reminder widget.
 *
 * Shows a live countdown to the daily order cut-off (20:00 Asia/Karachi).
 * The RED WARNING state appears ONLY during the final warning window
 * (WARNING_MINUTES = 60, mirroring the admin setting) before cut-off.
 *
 * updatePeriodMillis is 0 for this widget; instead an inexact repeating
 * AlarmManager alarm (every 15 min, battery-friendly, no exact-alarm
 * permission needed) drives onUpdate().
 */
public class ShopReminderWidget extends AppWidgetProvider {

    private static final int ALARM_REQUEST = 9001;
    private static final long INTERVAL_MS = 15 * 60 * 1000L;

    /** Warning red for the countdown text (argb). */
    private static final int COLOR_WARN = 0xFFFF5252;
    /** Normal white for the countdown text. */
    private static final int COLOR_NORMAL = 0xFFFFFFFF;

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, ShopReminderWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
        schedule(context);
    }

    @Override
    public void onDisabled(Context context) {
        cancel(context);
        super.onDisabled(context);
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_shop_reminder);
        long diff = WidgetHelper.millisToCutoff();
        long warnMs = (long) WidgetHelper.WARNING_MINUTES * 60L * 1000L;
        boolean warn = diff > 0 && diff <= warnMs;

        if (warn) {
            // WARNING: only inside the admin-configured final period.
            v.setTextViewText(R.id.title, "⚠️ آخری " + WidgetHelper.WARNING_MINUTES + " منٹ!");
            v.setTextViewText(R.id.countdown, "⏰ " + WidgetHelper.formatCountdown(diff));
            v.setInt(R.id.countdown, "setTextColor", COLOR_WARN);
            v.setTextViewText(R.id.subtitle, "ابھی آرڈر دیں!");
        } else if (diff <= 0) {
            v.setTextViewText(R.id.title, "🔒 کٹ آف گزر چکا");
            v.setTextViewText(R.id.countdown, WidgetHelper.formatCountdown(diff));
            v.setInt(R.id.countdown, "setTextColor", COLOR_NORMAL);
            v.setTextViewText(R.id.subtitle, "پیداوار: " + WidgetHelper.productionDate());
        } else {
            v.setTextViewText(R.id.title, "🕰️ آرڈر کٹ آف");
            v.setTextViewText(R.id.countdown, WidgetHelper.formatCountdown(diff));
            v.setInt(R.id.countdown, "setTextColor", COLOR_NORMAL);
            v.setTextViewText(R.id.subtitle, "پیداوار: " + WidgetHelper.productionDate());
        }

        v.setOnClickPendingIntent(R.id.widget_root,
                WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 15));
        mgr.updateAppWidget(widgetId, v);
    }

    private static PendingIntent alarmIntent(Context context) {
        Intent i = new Intent(context, ShopReminderWidget.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        return PendingIntent.getBroadcast(context, ALARM_REQUEST, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static void schedule(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setInexactRepeating(AlarmManager.RTC,
                    System.currentTimeMillis() + INTERVAL_MS, INTERVAL_MS, alarmIntent(context));
        }
    }

    static void cancel(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(alarmIntent(context));
        }
    }
}
