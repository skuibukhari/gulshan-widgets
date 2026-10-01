package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 2. Admin Vehicle / Supply widget (SEPARATE from the Daily Orders widget).
 * Shows today's date and the supply cut-off reminder.
 * Tap opens the Supply Calendar (سپلائی کیلنڈر).
 */
public class AdminSupplyWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, AdminSupplyWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_admin_supply);
        v.setTextViewText(R.id.title, "🚚 سپلائی / گاڑی");
        v.setTextViewText(R.id.line2, "آج: " + WidgetHelper.todayKhi());
        v.setTextViewText(R.id.line3, "کٹ آف: رات 8 بجے");
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_SUPPLY, 12));
        mgr.updateAppWidget(widgetId, v);
    }
}
