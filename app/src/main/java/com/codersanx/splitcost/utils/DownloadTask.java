package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.CHANGED;
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

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.codersanx.splitcost.R;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DownloadTask extends AsyncTask<Void, Void, Void> {
    private final RemoteFile fileToDownload;
    private final File localFile;
    private final ProgressListener listener;
    private final Activity ac;
    private final boolean isOnline;
    private final SyncDb syncDb;

    public DownloadTask(Activity ac, File localFile, ProgressListener listener, boolean isOnline, SyncDb syncdb) {
        this.ac = ac;
        this.fileToDownload = null;
        this.localFile = localFile;
        this.listener = listener;
        this.isOnline = isOnline;
        this.syncDb = syncdb;
    }

    public DownloadTask(Activity ac, RemoteFile fileToDownload, File localFile, ProgressListener listener, boolean isOnline) {
        this.ac = ac;
        this.fileToDownload = fileToDownload;
        this.localFile = localFile;
        this.listener = listener;
        this.isOnline = isOnline;
        this.syncDb = new SyncDb(-1, false, null, null);
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

            addValuesInOrig(tempExpense, expense);
            addValuesInOrig(tempIncome, income);
            addValuesInOrig(tempCategoryExpense, categoryExpense);
            addValuesInOrig(tempCategoryIncome, categoryIncome);

            changeValuesInOrig(tempExpense, expense, name);
            changeValuesInOrig(tempIncome, income, name);
            changeValuesInOrig(tempCategoryExpense, categoryExpense, name);
            changeValuesInOrig(tempCategoryIncome, categoryIncome, name);

            deleteTemp();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    uploadToCloud(name);
                    new Databases(ac, MAIN_SETTINGS).set(name + "lastSync", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    //syncDb.getAd().cancel();
                    ac.recreate();
                }
            }.execute();
            return;
        }

        deleteTemp();

        new Databases(ac, MAIN_SETTINGS).set(name + "lastSync", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
        ac.setResult(Activity.RESULT_OK);
        ac.finish();
    }

    private void uploadToCloud(String name) {
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

                            long newFileId = newRemoteFile.fileId();
                            new Databases(ac, MAIN_SETTINGS).set("id" + name, String.valueOf(newFileId));
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

    private void addValuesInOrig(Databases tempDb, Databases db) {
        for (Map.Entry<String, String> item : tempDb.readAll().entrySet()) {
            if (db.get(item.getKey()) == null) {
                db.set(item.getKey(), item.getValue());
            }
        }
    }

    private void changeValuesInOrig(Databases tempDb, Databases db, String name) {
        for (Map.Entry<String, String> item : db.readAll().entrySet()) {
            if (tempDb.get(item.getKey()) == null) {
                if (isOlder(getTimeOfLastSync(ac, name), getTimeOfKey(item.getKey()))) {
                    db.delete(item.getKey());
                }
            } else if (!tempDb.get(item.getKey()).equals(db.get(item.getKey()))) {
                if (!new Databases(ac, "TEMP" + name + CHANGED).get(item.getKey()).equals(tempDb.get(item.getKey()))) {
                    db.set(item.getKey(), tempDb.get(item.getKey()));
                }
            }
        }
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
