package com.gulshanfactory.widgets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal HTTP client for the Gulshan Factory server.
 * Logs in with username/password (session cookie) and fetches
 * live daily totals + tracker data for widgets.
 */
public class ApiClient {

    public static class ItemTotal {
        public String name;
        public double qty;
        public String unit;
        public int shopCount;
    }

    public static class ShopStatus {
        public int id;
        public String name;
        public boolean ordered;
        public int items;
    }

    public static class TrackerData {
        public int received;
        public int total;
        public List<ShopStatus> shops = new ArrayList<>();
    }

    private final String baseUrl;
    private String cookie;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /** Login and capture the session cookie. Returns true on success. */
    public boolean login(String username, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);
            HttpURLConnection c = post("/api/login", body.toString());
            int code = c.getResponseCode();
            if (code == 200) {
                // Capture session cookie (connect.sid or similar)
                String setCookie = c.getHeaderField("Set-Cookie");
                if (setCookie != null) {
                    // Keep only the cookie pair(s), drop attributes
                    StringBuilder sb = new StringBuilder();
                    for (String part : setCookie.split(",")) {
                        String pair = part.split(";", 2)[0].trim();
                        if (pair.contains("=")) {
                            if (sb.length() > 0) sb.append("; ");
                            sb.append(pair);
                        }
                    }
                    cookie = sb.toString();
                }
                readBody(c);
                return cookie != null && !cookie.isEmpty();
            }
            readBody(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Fetch per-product totals for the logged-in department. */
    public List<ItemTotal> fetchTotals() {
        List<ItemTotal> out = new ArrayList<>();
        try {
            HttpURLConnection c = get("/api/daily/totals");
            if (c.getResponseCode() == 200) {
                JSONArray arr = new JSONArray(readBody(c));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    double q = o.optDouble("total_qty", 0);
                    if (q <= 0) continue; // only items with orders
                    ItemTotal t = new ItemTotal();
                    t.name = o.optString("product_name", "");
                    t.qty = q;
                    t.unit = o.optString("unit_name", "");
                    t.shopCount = o.optInt("shop_count", 0);
                    out.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /** Fetch shop order tracker for the logged-in scope. */
    public TrackerData fetchTracker() {
        TrackerData d = new TrackerData();
        try {
            HttpURLConnection c = get("/api/daily/tracker");
            if (c.getResponseCode() == 200) {
                JSONObject o = new JSONObject(readBody(c));
                d.received = o.optInt("received", 0);
                d.total = o.optInt("total", 0);
                JSONArray arr = o.optJSONArray("shops");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject s = arr.getJSONObject(i);
                        ShopStatus st = new ShopStatus();
                        st.id = s.optInt("id", 0);
                        st.name = s.optString("name", "");
                        st.ordered = s.optBoolean("ordered", false);
                        st.items = s.optInt("items", 0);
                        d.shops.add(st);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    private HttpURLConnection post(String path, String json) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setConnectTimeout(15000);
        c.setReadTimeout(15000);
        c.setDoOutput(true);
        c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        c.setRequestProperty("Accept", "application/json");
        try (OutputStream os = c.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return c;
    }

    private HttpURLConnection get(String path) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setConnectTimeout(15000);
        c.setReadTimeout(15000);
        c.setRequestProperty("Accept", "application/json");
        if (cookie != null) c.setRequestProperty("Cookie", cookie);
        return c;
    }

    private String readBody(HttpURLConnection c) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(
                c.getResponseCode() < 400 ? c.getInputStream() : c.getErrorStream(),
                StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) sb.append(line);
        r.close();
        return sb.toString();
    }
}
