package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import java.util.List;

/**
 * Department Items widget — LIVE DATA (luxury).
 * Shows live order items with quantities for the logged-in department.
 * Requires one-time login via WidgetConfigActivity.
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
        v.setTextViewText(R.id.title, "🏭 ڈیپارٹمنٹ — آئٹم");
        v.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 13));

        if (!WidgetPrefs.hasLogin(context, widgetId)) {
            v.setTextViewText(R.id.item_count, "آئٹم: --");
            v.setTextViewText(R.id.shop_count, "پینڈنگ: --");
            v.setViewVisibility(R.id.login_hint, View.VISIBLE);
            mgr.updateAppWidget(widgetId, v);
            return;
        }

        v.setTextViewText(R.id.item_count, "آئٹم: ...");
        v.setViewVisibility(R.id.login_hint, View.GONE);
        mgr.updateAppWidget(widgetId, v);

        final Context appCtx = context.getApplicationContext();
        new Thread(() -> {
            try {
                String u = WidgetPrefs.getUsername(appCtx, widgetId);
                String p = WidgetPrefs.getPassword(appCtx, widgetId);
                ApiClient api = new ApiClient(WidgetHelper.APP_URL);
                List<ApiClient.ItemTotal> items = null;
                ApiClient.TrackerData tracker = null;
                if (api.login(u, p)) {
                    items = api.fetchTotals();
                    tracker = api.fetchTracker();
                }
                RemoteViews rv = new RemoteViews(appCtx.getPackageName(), R.layout.widget_dept_items);
                rv.setTextViewText(R.id.title, "🏭 ڈیپارٹمنٹ — آئٹم");
                rv.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
                rv.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(appCtx, WidgetHelper.URL_DAILY, 13));

                if (items != null && !items.isEmpty()) {
                    rv.setTextViewText(R.id.item_count, "آئٹم: " + items.size());
                    if (tracker != null) {
                        int pending = tracker.total - tracker.received;
                        rv.setTextViewText(R.id.shop_count, "پینڈنگ: " + pending);
                    }
                    int[] cardIds = {R.id.item1_card, R.id.item2_card, R.id.item3_card};
                    int[] nameIds = {R.id.item1, R.id.item2, R.id.item3};
                    int[] qtyIds = {R.id.qty1, R.id.qty2, R.id.qty3};
                    int shown = Math.min(3, items.size());
                    for (int i = 0; i < shown; i++) {
                        ApiClient.ItemTotal t = items.get(i);
                        rv.setViewVisibility(cardIds[i], View.VISIBLE);
                        rv.setTextViewText(nameIds[i], t.name);
                        String q = formatQty(t.qty) + (t.unit.isEmpty() ? "" : " " + t.unit);
                        rv.setTextViewText(qtyIds[i], "×" + q);
                    }
                    int remaining = items.size() - shown;
                    if (remaining > 0) {
                        rv.setViewVisibility(R.id.more_items, View.VISIBLE);
                        rv.setTextViewText(R.id.more_items, "+ " + remaining + " آئٹم اور");
                    }
                } else {
                    rv.setTextViewText(R.id.item_count, "آئٹم: 0");
                    rv.setTextViewText(R.id.shop_count, "ابھی کوئی آرڈر نہیں");
                }
                mgr.updateAppWidget(widgetId, rv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String formatQty(double q) {
        if (q == Math.floor(q)) return String.valueOf((long) q);
        return String.valueOf(q);
    }
}
