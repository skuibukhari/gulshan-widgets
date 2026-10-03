package com.gulshanfactory.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Shown when a live-data widget is added. The user logs in once with their
 * department/shop account; the widget then fetches live data on its own.
 */
public class WidgetConfigActivity extends Activity {

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText etUser, etPass;
    private TextView tvError;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.widget_config);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        etUser = findViewById(R.id.et_username);
        etPass = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String u = etUser.getText().toString().trim();
        String p = etPass.getText().toString();
        if (u.isEmpty() || p.isEmpty()) {
            tvError.setText("یوزر نیم اور پاس ورڈ لکھیں");
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        btnLogin.setEnabled(false);
        tvError.setVisibility(View.GONE);

        new Thread(() -> {
            ApiClient api = new ApiClient(WidgetHelper.APP_URL);
            boolean ok = api.login(u, p);
            runOnUiThread(() -> {
                if (ok) {
                    WidgetPrefs.saveLogin(this, widgetId, u, p);
                    // Trigger an immediate update
                    AppWidgetManager mgr = AppWidgetManager.getInstance(this);
                    DeptShopsWidget.updateOne(this, mgr, widgetId);
                    DeptItemsWidget.updateOne(this, mgr, widgetId);
                    Intent result = new Intent();
                    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    tvError.setText("لاگ اِن ناکام — یوزر نیم/پاس ورڈ چیک کریں");
                    tvError.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(true);
                }
            });
        }).start();
    }
}
