package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * 7. Supplier widget (e.g. Naveed → Premium Topanwala).
 * Reminder for the supplier's assigned shops.
 * Tap opens the app (supplier sees their own assigned shops after login).
 */
public class SupplierWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        if (ids == null || ids.length == 0) {
            ids = WidgetHelper.allWidgetIds(context, mgr, SupplierWidget.class);
        }
        for (int id : ids) {
            updateOne(context, mgr, id);
        }
    }

    static void updateOne(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_supplier);
        v.setTextViewText(R.id.title, "🏭 سپلائر");
        v.setTextViewText(R.id.big_count, "0/0");
        v.setTextViewText(R.id.subtitle, "میری دکانوں کا آرڈر");
        v.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setProgressBar(R.id.progress, 100, 0, false);
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.APP_URL, 17));
        mgr.updateAppWidget(widgetId, v);
    }
}
