package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 6. Individual Vehicle widget (for drivers like Arif and Rauf).
 * Shows today's duty date and the cut-off countdown.
 * Tap opens the app (driver sees their own assigned shops after login).
 */
public class VehicleWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, VehicleWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_vehicle);
        v.setTextViewText(R.id.title, "🚚 میری ڈیوٹی");
        v.setTextViewText(R.id.line2, "آج: " + WidgetHelper.todayKhi());
        v.setTextViewText(R.id.line3, "کٹ آف تک: " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.APP_URL, 16));
        mgr.updateAppWidget(widgetId, v);
    }
}
