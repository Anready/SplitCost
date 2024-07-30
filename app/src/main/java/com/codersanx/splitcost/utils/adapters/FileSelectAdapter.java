package com.codersanx.splitcost.utils.adapters;

import static com.codersanx.splitcost.utils.Utils.getHost;
import static com.codersanx.splitcost.utils.Utils.getToken;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.utils.network.DownloadTask;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.ProgressListener;
import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileSelectAdapter extends BaseAdapter {
    private final List<RemoteEntry> entries;
    private final LayoutInflater inflater;
    private final Activity ac;

    public FileSelectAdapter(Activity ac, List<RemoteEntry> entries) {
        this.ac = ac;
        this.entries = entries;
        this.inflater = LayoutInflater.from(ac);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.select_file_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textView);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        textView.setText(entries.get(position).name());
        imageView.setImageResource(entries.get(position).isFolder() ? R.drawable.ic_folder : R.drawable.ic_file);

        convertView.setOnClickListener(v -> {
            if (entries.get(position).name().equals("..")) {
                getObjectsByFolderId(ac, false, entries.get(position).id().replace("d", ""));
            }else if (entries.get(position).isFolder()) {
                getObjectsByFolderId(ac, false, entries.get(position).id().replace("d", ""));
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ac, R.style.RoundedDialog);
                alertDialogBuilder.setTitle(ac.getResources().getString(R.string.onlineDatabase));
                alertDialogBuilder.setMessage(ac.getResources().getString(R.string.onlineDbDescription));

                RemoteFile fileToDownload = entries.get(position).asFile();
                File localFile = new File(ac.getFilesDir(), fileToDownload.name());
                ProgressListener listener = (done, total) -> System.out.println(done + "/" + total);

                alertDialogBuilder.setPositiveButton(ac.getResources().getString(R.string.yes), (dialog, which) -> {
                    DownloadTask downloadTask = new DownloadTask(ac, fileToDownload, localFile, listener, true);
                    startDownload(downloadTask);
                });


                alertDialogBuilder.setNegativeButton(ac.getResources().getString(R.string.no), (dialog, which) -> {
                    DownloadTask downloadTask = new DownloadTask(ac, fileToDownload, localFile, listener, false);
                    startDownload(downloadTask);
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        return convertView;
    }

    public static void getObjectsByFolderId(Activity ac, boolean isRoot, String id) {
        ApiClient apiClient = PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(getToken(ac)))
                .apiHost(getHost(ac))
                .create();


        Call<RemoteFolder> call = apiClient.listFolder(RemoteFolder.ROOT_FOLDER_ID);
        if (!isRoot) call = apiClient.listFolder(Long.parseLong(id));
        call.enqueue(new Callback<RemoteFolder>() {
            @Override
            public void onResponse(Call<RemoteFolder> call, RemoteFolder response) {
                List<RemoteEntry> texts = new ArrayList<>();

                for (RemoteEntry entry : response.children()) {
                    if (entry.isFolder()) texts.add(entry);
                    if (!entry.isFolder() && entry.name().endsWith(".sce")) texts.add(entry);
                }

                if (!isRoot) texts.add(0, new RemoteEntry() {
                    @Override
                    public String id() {
                        return String.valueOf(response.parentFolderId());
                    }

                    @Override
                    public String name() {
                        return "..";
                    }

                    @Override
                    public Date lastModified() {
                        return null;
                    }

                    @Override
                    public Date created() {
                        return null;
                    }

                    @Override
                    public long parentFolderId() {
                        return response.parentFolderId();
                    }

                    @Override
                    public boolean isFile() {
                        return false;
                    }

                    @Override
                    public boolean isFolder() {
                        return false;
                    }

                    @Override
                    public RemoteFolder asFolder() {
                        return null;
                    }

                    @Override
                    public RemoteFile asFile() {
                        return null;
                    }

                    @Override
                    public RemoteEntry copy(RemoteFolder toFolder) {
                        return null;
                    }

                    @Override
                    public RemoteEntry copy(RemoteFolder toFolder, boolean overwrite)  {
                        return null;
                    }

                    @Override
                    public RemoteEntry move(RemoteFolder toFolder) {
                        return null;
                    }

                    @Override
                    public RemoteEntry rename(String newFilename) {
                        return null;
                    }

                    @Override
                    public boolean delete() {
                        return false;
                    }

                    @Override
                    public boolean canRead() {
                        return false;
                    }

                    @Override
                    public boolean canModify() {
                        return false;
                    }

                    @Override
                    public boolean canDelete() {
                        return false;
                    }

                    @Override
                    public boolean isMine() {
                        return false;
                    }

                    @Override
                    public boolean isShared() {
                        return false;
                    }
                });

                ListView listView = ac.findViewById(R.id.listView);
                FileSelectAdapter adapter = new FileSelectAdapter(ac, texts);
                ac.runOnUiThread(() -> listView.setAdapter(adapter));
            }

            @Override
            public void onFailure(Call<RemoteFolder> call, Throwable t) {
            }
        });
    }

    public static void getObjectsByFolderId(Activity ac, boolean isRoot) {
        if (isRoot) getObjectsByFolderId(ac, true, "");
    }

    private void startDownload(DownloadTask dt) {
        ac.runOnUiThread(() -> {
            Toast.makeText(ac, "File download was started", Toast.LENGTH_SHORT).show();
            dt.execute();
        });
    }
}
