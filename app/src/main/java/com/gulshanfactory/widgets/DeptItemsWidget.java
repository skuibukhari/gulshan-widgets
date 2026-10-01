package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 3. Department widget — ITEM-WISE view.
 * For Bread / Dry / Namkeen / Fresh department accounts.
 * Tap opens the Daily Orders board.
 */
public class DeptItemsWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, DeptItemsWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_dept_items);
        v.setTextViewText(R.id.title, "🗂 آئٹم وائز");
        v.setTextViewText(R.id.line2, "پیداوار: " + WidgetHelper.productionDate());
        v.setTextViewText(R.id.line3, "محکمہ: بریڈ / ڈرائی / نمکین / فریش");
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 13));
        mgr.updateAppWidget(widgetId, v);
    }
}
