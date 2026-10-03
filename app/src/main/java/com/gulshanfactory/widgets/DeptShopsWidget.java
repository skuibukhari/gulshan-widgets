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
        v.setTextViewText(R.id.title, "🏪 ڈیپارٹمنٹ دکانیں");
        v.setTextViewText(R.id.big_count, "0/0");
        v.setTextViewText(R.id.subtitle, "دکانوں کا آرڈر موصول");
        v.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setProgressBar(R.id.progress, 100, 0, false);
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 14));
        mgr.updateAppWidget(widgetId, v);
    }
}
