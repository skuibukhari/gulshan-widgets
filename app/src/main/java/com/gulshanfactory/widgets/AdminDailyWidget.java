package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 1. Admin Daily Orders widget.
 * Shows today's production date and the live countdown to the daily
 * order cut-off. Tap opens the Daily Orders board (روزانہ آرڈر).
 */
public class AdminDailyWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, AdminDailyWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_admin_daily);
        v.setTextViewText(R.id.title, "📊 روزانہ آرڈر");
        v.setTextViewText(R.id.line2, "پیداوار: " + WidgetHelper.productionDate());
        v.setTextViewText(R.id.line3, "کٹ آف تک: " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 11));
        mgr.updateAppWidget(widgetId, v);
    }
}
