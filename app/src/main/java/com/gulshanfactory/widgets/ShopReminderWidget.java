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
            // WARNING: only inside the admin-configured final period - RED background.
            v.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_shop_warn);
            v.setTextViewText(R.id.title, "🏪 میری دکان");
            v.setViewVisibility(R.id.warn_banner, android.view.View.VISIBLE);
            v.setTextViewText(R.id.warn_banner, "⚠️ آخری " + WidgetHelper.WARNING_MINUTES + " منٹ!");
            v.setTextViewText(R.id.countdown, WidgetHelper.formatCountdown(diff));
            v.setTextViewText(R.id.subtitle, "ابھی آرڈر دیں!");
            v.setTextViewText(R.id.order_status, "");
        } else if (diff <= 0) {
            v.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_shop);
            v.setTextViewText(R.id.title, "🏪 میری دکان");
            v.setViewVisibility(R.id.warn_banner, android.view.View.GONE);
            v.setTextViewText(R.id.countdown, WidgetHelper.formatCountdown(diff));
            v.setTextViewText(R.id.subtitle, "پیداوار: " + WidgetHelper.productionDate());
            v.setTextViewText(R.id.order_status, "");
        } else {
            v.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_shop);
            v.setTextViewText(R.id.title, "🏪 میری دکان");
            v.setViewVisibility(R.id.warn_banner, android.view.View.GONE);
            v.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(diff));
            v.setTextViewText(R.id.subtitle, "آرڈر کا وقت باقی");
            v.setTextViewText(R.id.order_status, "پیداوار: " + WidgetHelper.productionDate());
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
