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
        v.setTextViewText(R.id.title, "🏭 ڈیپارٹمنٹ");
        v.setTextViewText(R.id.item_count, "آئٹم: --");
        v.setTextViewText(R.id.shop_count, "پینڈنگ: --");
        v.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        // Items list: populated from live data when available; tap opens the app
        v.setViewVisibility(R.id.item1, android.view.View.GONE);
        v.setViewVisibility(R.id.item2, android.view.View.GONE);
        v.setViewVisibility(R.id.item3, android.view.View.GONE);
        v.setViewVisibility(R.id.item4, android.view.View.GONE);
        v.setViewVisibility(R.id.more_items, android.view.View.GONE);
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 13));
        mgr.updateAppWidget(widgetId, v);
    }
}
