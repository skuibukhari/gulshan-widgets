package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 4. Department widget — SHOP-WISE view.
 * For Bread / Dry / Namkeen / Fresh department accounts.
 * Tap opens the Daily Orders board.
 */
public class DeptShopsWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, DeptShopsWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_dept_shops);
        v.setTextViewText(R.id.title, "🏪 دکان وائز");
        v.setTextViewText(R.id.line2, "پیداوار: " + WidgetHelper.productionDate());
        v.setTextViewText(R.id.line3, "کٹ آف تک: " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 14));
        mgr.updateAppWidget(widgetId, v);
    }
}
