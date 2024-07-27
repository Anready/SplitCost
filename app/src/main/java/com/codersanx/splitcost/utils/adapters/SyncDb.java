package com.codersanx.splitcost.utils.adapters;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class SyncDb {
    private final long id;
    private final boolean isSync;
    private final DialogInterface ad;
    private final String additionalName;

    public SyncDb(long id, boolean isSync, DialogInterface ad, String additionalName) {
        this.id = id;
        this.isSync = isSync;
        this.ad = ad;
        this.additionalName = additionalName;
    }

    public long getId() {
        return id;
    }

    public boolean isSync() {
        return isSync;
    }

    public DialogInterface getAd() {
        return ad;
    }

    public String getAdditionalName() {
        return additionalName;
    }
}

