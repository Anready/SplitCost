package com.codersanx.splitcost.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetUpdate {

    public interface UpdateCallback {
        void onUpdateReceived(String[] version);
    }

    private final String urlString;
    private final UpdateCallback callback;
    private final Context context;
    private final ExecutorService executorService;

    public GetUpdate(String url, UpdateCallback callback, Context context) {
        this.urlString = url;
        this.callback = callback;
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void getUpdateInformation() {
        Future<String> future = executorService.submit(this::receiveInfo);
        try {
            String result = future.get();
            processResult(result);
        } catch (Exception ignored) {
        }
    }

    private String receiveInfo() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            result = buffer.toString();
        } catch (IOException ignored) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ignored) {
                }
            }
        }
        return result;
    }

    protected void processResult(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject updateObj = jsonObject.getJSONObject(context.getApplicationContext().getPackageName());

                String versionApi = updateObj.getString("version");
                String versionCodeApi = updateObj.getString("version_code");
                String description = updateObj.getString("description");

                String versionName = null;
                String versionCode = null;

                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    versionName = packageInfo.versionName;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        versionCode = String.valueOf(packageInfo.getLongVersionCode());
                    } else {
                        versionCode = String.valueOf(packageInfo.versionCode);
                    }
                } catch (PackageManager.NameNotFoundException ignored) {
                }

                if (!versionApi.equals(versionName) && !versionCodeApi.equals(versionCode)) {
                    JSONObject storesArray = updateObj.getJSONObject("stores");
                    Iterator<String> keys = storesArray.keys();
                    String link = null;

                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (key.contains("/")) {
                            link = key;
                            break;
                        }

                        PackageManager packageManager = context.getPackageManager();

                        try {
                            packageManager.getPackageInfo(key, PackageManager.GET_ACTIVITIES);
                        } catch (PackageManager.NameNotFoundException e) {
                            continue;
                        }

                        link = storesArray.getString(key);
                        break;
                    }

                    String[] updateInfo = {description, link};
                    if (callback != null) {
                        callback.onUpdateReceived(updateInfo);
                    }
                }
            } catch (JSONException ignored) {
            }
        }
    }
}
