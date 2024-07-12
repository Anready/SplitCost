package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Utils.isDatabaseOnline;
import static com.codersanx.splitcost.utils.Utils.isInternetAvailable;
import static com.codersanx.splitcost.utils.Utils.synchronizeDb;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.codersanx.splitcost.R;

public class SyncDbOnExit extends Worker {

    private final Context context;
    private static Activity activityReference;

    public SyncDbOnExit(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    public static void setActivity(Activity activity) {
        activityReference = activity;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (activityReference != null) {
            if (isInternetAvailable(activityReference.getApplication()) && isDatabaseOnline(context)) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, context.getResources().getString(R.string.syncinc), Toast.LENGTH_SHORT).show());
                synchronizeDb(context);
            }
        }

        return Result.success();
    }
}
