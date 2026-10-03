package com.gulshanfactory.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Department Shops widget — LIVE DATA (Design B luxury).
 * Shows received/total shops, live shop status with names,
 * pending countdown. Requires one-time login via WidgetConfigActivity.
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
        try {
        updateOneSafe(context, mgr, widgetId);
        } catch (Exception e) { e.printStackTrace(); }
    }

    static void updateOneSafe(Context context, AppWidgetManager mgr, int widgetId) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_dept_shops);
        v.setTextViewText(R.id.title, "🏪 ڈیپارٹمنٹ — دکانیں");
        v.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
        v.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(context, WidgetHelper.URL_DAILY, 14));

        if (!WidgetPrefs.hasLogin(context, widgetId)) {
            // Tap opens login screen to fix missing login
            android.content.Intent cfg = new android.content.Intent(context, WidgetConfigActivity.class);
            cfg.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            cfg.putExtra("relogin", true);
            cfg.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            android.app.PendingIntent pi = android.app.PendingIntent.getActivity(context, widgetId,
                    cfg, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
            v.setOnClickPendingIntent(R.id.widget_root, pi);
            v.setTextViewText(R.id.big_count, "--/--");
            v.setTextViewText(R.id.subtitle, "دکانوں کا آرڈر موصول");
            v.setProgressBar(R.id.progress, 100, 0, false);
            v.setViewVisibility(R.id.login_hint, View.VISIBLE);
            mgr.updateAppWidget(widgetId, v);
            return;
        }

        // Show loading, then fetch live data in background
        v.setTextViewText(R.id.big_count, "...");
        v.setViewVisibility(R.id.login_hint, View.GONE);
        mgr.updateAppWidget(widgetId, v);

        final Context appCtx = context.getApplicationContext();
        new Thread(() -> {
            try {
                String u = WidgetPrefs.getUsername(appCtx, widgetId);
                String p = WidgetPrefs.getPassword(appCtx, widgetId);
                ApiClient api = new ApiClient(WidgetHelper.APP_URL);
                ApiClient.TrackerData d = null;
                if (api.login(u, p)) {
                    d = api.fetchTracker();
                }
                RemoteViews rv = new RemoteViews(appCtx.getPackageName(), R.layout.widget_dept_shops);
                rv.setTextViewText(R.id.title, "🏪 ڈیپارٹمنٹ — دکانیں");
                rv.setTextViewText(R.id.countdown, "⏳ " + WidgetHelper.formatCountdown(WidgetHelper.millisToCutoff()));
                rv.setOnClickPendingIntent(R.id.widget_root, WidgetHelper.openUrl(appCtx, WidgetHelper.URL_DAILY, 14));

                if (d != null && d.total > 0) {
                    rv.setTextViewText(R.id.big_count, d.received + "/" + d.total);
                    rv.setTextViewText(R.id.subtitle, "دکانوں کا آرڈر موصول");
                    int pct = (int) (d.received * 100L / d.total);
                    rv.setProgressBar(R.id.progress, 100, pct, false);

                    // Show up to 3 shops: pending first, then received
                    int shown = 0;
                    int[] cardIds = {R.id.shop1_card, R.id.shop2_card, R.id.shop3_card};
                    int[] textIds = {R.id.shop1, R.id.shop2, R.id.shop3};
                    // Pending shops first
                    for (ApiClient.ShopStatus s : d.shops) {
                        if (shown >= 3) break;
                        if (!s.ordered) {
                            rv.setViewVisibility(cardIds[shown], View.VISIBLE);
                            rv.setTextViewText(textIds[shown], "⏳ " + s.name);
                            shown++;
                        }
                    }
                    // Then received
                    for (ApiClient.ShopStatus s : d.shops) {
                        if (shown >= 3) break;
                        if (s.ordered) {
                            rv.setViewVisibility(cardIds[shown], View.VISIBLE);
                            rv.setTextViewText(textIds[shown], "✅ " + s.name + " — آ گیا");
                            shown++;
                        }
                    }
                    int remaining = d.shops.size() - shown;
                    if (remaining > 0) {
                        rv.setViewVisibility(R.id.more_shops, View.VISIBLE);
                        rv.setTextViewText(R.id.more_shops, "+ " + remaining + " دکانیں اور");
                    }
                } else {
                    rv.setTextViewText(R.id.big_count, "0/0");
                    rv.setTextViewText(R.id.subtitle, "ڈیٹا نہیں ملا — دوبارہ کوشش کریں");
                    rv.setProgressBar(R.id.progress, 100, 0, false);
                }
                mgr.updateAppWidget(widgetId, rv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
