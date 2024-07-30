package com.codersanx.splitcost.utils.network;

import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.ONLINE;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.getDbId;
import static com.codersanx.splitcost.utils.Utils.getFolderId;
import static com.codersanx.splitcost.utils.Utils.getHost;
import static com.codersanx.splitcost.utils.Utils.getTimeOfLastSync;
import static com.codersanx.splitcost.utils.Utils.getToken;
import static com.codersanx.splitcost.utils.Utils.renameFile;
import static com.codersanx.splitcost.utils.Zip.packFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.utils.Databases;
import com.codersanx.splitcost.utils.Zip;
import com.codersanx.splitcost.utils.adapters.AskDataItem;
import com.codersanx.splitcost.utils.adapters.SyncDb;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.DataSource;
import com.pcloud.sdk.DownloadOptions;
import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.ProgressListener;
import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class DownloadTask extends AsyncTask<Void, Void, Void> {
    private final RemoteFile fileToDownload;
    private final File localFile;
    private final ProgressListener listener;
    private final Activity ac;
    private final boolean isOnline;
    private final SyncDb syncDb;
    private final DownloadTaskCallback callback;
    private boolean isNoAlert = true;

    public interface DownloadTaskCallback {
        void onDownloadTaskEnd();
    }

    public DownloadTask(Activity ac, File localFile, ProgressListener listener, boolean isOnline, SyncDb syncdb) {
        this.ac = ac;
        this.fileToDownload = null;
        this.localFile = localFile;
        this.listener = listener;
        this.isOnline = isOnline;
        this.syncDb = syncdb;
        this.callback = (DownloadTaskCallback) ac;
    }

    public DownloadTask(Activity ac, RemoteFile fileToDownload, File localFile, ProgressListener listener, boolean isOnline) {
        this.ac = ac;
        this.fileToDownload = fileToDownload;
        this.localFile = localFile;
        this.listener = listener;
        this.isOnline = isOnline;
        this.syncDb = new SyncDb(-1, false, null, null);
        this.callback = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (syncDb.getId() != -1) {
                ApiClient apiClient = PCloudSdk.newClientBuilder()
                        .authenticator(Authenticators.newOAuthAuthenticator(getToken(ac)))
                        .apiHost(getHost(ac))
                        .create();

                DownloadOptions options = DownloadOptions.create()
                        .skipFilename(true)
                        .forceDownload(false)
                        .contentType("application/vnd.etsi.asic-e+zip")
                        .build();

                apiClient.createFileLink(syncDb.getId(), options).execute().download(DataSink.create(localFile), listener);
            } else fileToDownload.download(DataSink.create(localFile), listener);
        } catch (IOException | ApiError e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Update the UI if needed after download is complete
        Toast.makeText(ac, "File downloaded successfully", Toast.LENGTH_SHORT).show();
        boolean resultOfExtract = Zip.extractZip(ac, localFile.getName(), syncDb.isSync(), syncDb.getAdditionalName());

        if (!resultOfExtract) {
            Toast.makeText(ac, ac.getResources().getString(R.string.incorrectFile), Toast.LENGTH_SHORT).show();
            deleteTemp();
            return;
        }

        String name = localFile.getName().replace(".sce", "");
        if (isOnline) {
            if (syncDb.getId() != -1)
                new Databases(ac, MAIN_SETTINGS).set("id" + name, String.valueOf(syncDb.getId()));
            else new Databases(ac, MAIN_SETTINGS).set("id" + name, fileToDownload.id());

            if (fileToDownload != null)
                new Databases(ac, MAIN_SETTINGS).set("idFolder" + name, String.valueOf(fileToDownload.parentFolderId()));
            else
                new Databases(ac, MAIN_SETTINGS).set("idFolder" + name, String.valueOf(getFolderId(ac, name)));
            new Databases(ac, name + MAIN_SETTINGS).set(ONLINE, TRUE);
        } else {
            new Databases(ac, name + MAIN_SETTINGS).set(ONLINE, FALSE);
        }

        if (syncDb.isSync()) {
            Databases tempExpense = new Databases(ac, "TEMP" + name + EXPENSES);
            Databases tempIncome = new Databases(ac, "TEMP" + name + INCOMES);
            Databases tempCategoryExpense = new Databases(ac, "TEMP" + name + CATEGORY + EXPENSES);
            Databases tempCategoryIncome = new Databases(ac, "TEMP" + name + CATEGORY + INCOMES);

            Databases expense = new Databases(ac, name + EXPENSES);
            Databases income = new Databases(ac, name + INCOMES);
            Databases categoryExpense = new Databases(ac, name + CATEGORY + EXPENSES);
            Databases categoryIncome = new Databases(ac, name + CATEGORY + INCOMES);

            addValuesInOrig(tempExpense, expense, name, true);
            addValuesInOrig(tempIncome, income, name);
            addCategories(tempCategoryExpense, categoryExpense, false);
            addCategories(tempCategoryIncome, categoryIncome, true);

            deleteTemp();
            return;
        }

        deleteTemp();

        new Databases(ac, MAIN_SETTINGS).set(name + "lastSync", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
        ac.setResult(Activity.RESULT_OK);
        ac.finish();
    }

    private void addCategories(Databases tempDb, Databases db, boolean isLast) {
        List<AskDataItem> allDataToShow = Collections.emptyList();
        for (Map.Entry<String, String> item : tempDb.readAll().entrySet()) {
            if (db.get(item.getKey()) != null && !db.get(item.getKey()).equals(item.getValue())){
                allDataToShow.add(new AskDataItem(new String[] {
                        item.getKey(), item.getValue(), db.get(item.getKey()),
                        String.format(ac.getResources().getString(R.string.descriptionWhatToDo),
                                item.getValue(), "",
                                "Cloud: " + item.getKey() + (item.getValue().equals(TRUE) ? ": EXIST" : ": NOT EXIST") +
                                        "\nLocal: " + item.getKey() + (db.get(item.getKey()).equals(TRUE) ? ": EXIST" : ": NOT EXIST")
                        )}, db, false
                ));

            } else if (db.get(item.getKey()) == null) {
                allDataToShow.add(new AskDataItem(new String[] {
                                item.getKey(), item.getValue(), null,
                                String.format(ac.getResources().getString(R.string.descriptionWhatToDo),
                                        item.getKey(), "",
                                        ac.getResources().getString(R.string.addOrRemove))}
                        , db, true
                ));

            }
        }

        if (!allDataToShow.isEmpty()) askAboutData(allDataToShow, false);
        else if (isLast && isNoAlert) callback.onDownloadTaskEnd();
    }

    private void addValuesInOrig(Databases tempDb, Databases db, String name){
        addValuesInOrig(tempDb, db, name, false);
    }

    private void addValuesInOrig(Databases tempDb, Databases db, String name, boolean isLast) {
        List<AskDataItem> allDataToShow = new ArrayList<>();
        for (Map.Entry<String, String> item : tempDb.readAll().entrySet()) {
            String[] itemSplit = item.getKey().split("@");
            if (db.get(item.getKey()) == null && !isOlder(getTimeOfLastSync(ac, name), getTimeOfKey(item.getKey()))) {
                db.set(item.getKey(), item.getValue());
            }else if (db.get(item.getKey()) != null && !db.get(item.getKey()).equals(item.getValue())){
                allDataToShow.add(new AskDataItem(new String[] {
                        item.getKey(), item.getValue(), db.get(item.getKey()),
                        String.format(ac.getResources().getString(R.string.descriptionWhatToDo),
                                itemSplit[0], itemSplit[1],
                                "Cloud: " + itemSplit[0] + " " + itemSplit[1] + ": " + item.getValue() +
                                        "\nLocal: " + itemSplit[0] + " " + itemSplit[1] + ": " + db.get(item.getKey())
                        )}, db, false
                ));
            } else if (db.get(item.getKey()) == null){
                allDataToShow.add(new AskDataItem(new String[] {
                        item.getKey(), item.getValue(), null,
                        String.format(ac.getResources().getString(R.string.descriptionWhatToDo),
                                itemSplit[0], itemSplit[1],
                                ac.getResources().getString(R.string.addOrRemove))}
                        , db, true
                ));
            }
        }

        if (!allDataToShow.isEmpty()) askAboutData(allDataToShow, isLast);
    }


    private AlertDialog alertDialog;

    private void askAboutData(List<AskDataItem> allData, boolean isLast) {
        isNoAlert = false;
        ac.runOnUiThread(() -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ac, R.style.RoundedDialog);
            alertDialogBuilder.setTitle(ac.getResources().getString(R.string.whatToDo));
            alertDialogBuilder.setCancelable(false);

            ScrollView scrollView = new ScrollView(ac);
            LinearLayout layout = new LinearLayout(ac);
            layout.setPadding(20,20,20,20);
            layout.setOrientation(LinearLayout.VERTICAL);

            for (AskDataItem item : allData) {
                TextView textView = new TextView(ac);
                textView.setText(item.data[3]);
                layout.addView(textView);

                LinearLayout buttonLayout = new LinearLayout(ac);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

                Button cloudButton = new Button(ac);
                cloudButton.setText(ac.getResources().getString(R.string.fromCloud));
                cloudButton.setOnClickListener(v -> {
                    item.db.set(item.data[0], item.data[1]);
                    buttonLayout.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);

                    if (!hasVisibleElements(layout)) {
                        if (isLast) callback.onDownloadTaskEnd();
                        callback.onDownloadTaskEnd();
                        alertDialog.dismiss(); // Закрываем диалог
                    }
                });

                Button localButton = new Button(ac);
                localButton.setText(ac.getResources().getString(R.string.fromLocal));
                localButton.setOnClickListener(v -> {
                    if (item.isAddOrRemove) item.db.delete(item.data[0]);
                    else item.db.set(item.data[0], item.data[2]);

                    buttonLayout.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);

                    if (!hasVisibleElements(layout)) {
                        if (isLast) callback.onDownloadTaskEnd();
                        callback.onDownloadTaskEnd();
                        alertDialog.dismiss(); // Закрываем диалог
                    }
                });

                buttonLayout.addView(cloudButton);
                buttonLayout.addView(localButton);
                layout.addView(buttonLayout);
            }

            scrollView.addView(layout);
            alertDialogBuilder.setView(scrollView);

            alertDialogBuilder.setNegativeButton(ac.getResources().getString(R.string.fromCloud), (dialog, which) -> {
                for (AskDataItem item : allData) {
                    item.db.set(item.data[0], item.data[1]);
                }

                if (isLast) callback.onDownloadTaskEnd();
                dialog.dismiss();
            });

            alertDialogBuilder.setPositiveButton(ac.getResources().getString(R.string.fromLocal), (dialog, which) -> {
                for (AskDataItem item : allData) {
                    if (item.isAddOrRemove) item.db.delete(item.data[0]);
                    else item.db.set(item.data[0], item.data[2]);
                }

                if (isLast) callback.onDownloadTaskEnd();
                dialog.dismiss();
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    private boolean hasVisibleElements(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                return true;
            }
        }
        return false;
    }

    public static void uploadToCloud(Activity ac, String name) {
        packFile(ac, name);
        String filePath = name + ".zip";
        String newName = name + ".sce";

        File oldFile = new File(ac.getFilesDir(), filePath);
        renameFile(oldFile, newName);

        File file = new File(ac.getFilesDir(), newName);

        ProgressListener listener = (done, total) -> System.out.format("\rUploading... %.1f\n", ((double) done / (double) total) * 100d);

        ApiClient apiClient = PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(getToken(ac)))
                .apiHost(getHost(ac))
                .create();

        Call<RemoteFolder> call = apiClient.listFolder(getFolderId(ac, name));
        call.enqueue(new Callback<RemoteFolder>() {
            @Override
            public void onResponse(Call<RemoteFolder> call, RemoteFolder response) {
                // Successful response
                for (RemoteEntry entry : response.children()) {
                    if (Long.parseLong(entry.id().replace("f", "").replace("d", "")) == getDbId(ac, name)) {
                        if (entry.asFile() != null) {
                            try {
                                apiClient.deleteFile(entry.asFile()).execute();
                            } catch (IOException | ApiError e) {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            RemoteFile newRemoteFile = apiClient.createFile(
                                    getFolderId(ac, name),
                                    newName,
                                    DataSource.create(file),
                                    new Date(file.lastModified()),
                                    listener
                            ).execute();

                            new Databases(ac, MAIN_SETTINGS).set(name + "lastSync", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                            new Databases(ac, MAIN_SETTINGS).set(name + "lastSyncFile", String.valueOf(new Date(file.lastModified())));
                        } catch (IOException | ApiError e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoteFolder> call, Throwable t) {
                // Call failed with an error.
            }
        });
    }

    private String getTimeOfKey(String key) {
        return key.substring(0, 19);
    }

    private boolean isOlder(String dateOfSync, String dateOfKey) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date1 = dateFormat.parse(dateOfSync);
            Date date2 = dateFormat.parse(dateOfKey);

            return date1.after(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deleteTemp() {
        String name = "TEMP" + localFile.getName().replace(".sce", "");

        String[] files = {name + INCOMES, name + EXPENSES, name + CATEGORY + INCOMES,
                name + CATEGORY + EXPENSES, name + MAIN_SETTINGS
        };

        for (String file : files) {
            new File(ac.getFilesDir().getParent() + "/shared_prefs/", file + ".xml").delete();
        }
    }
}
