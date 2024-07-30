package com.codersanx.splitcost.utils.adapters;

import com.codersanx.splitcost.utils.Databases;

public class AskDataItem {
    public final String[] data;
    public final Databases db;
    public final boolean isAddOrRemove;

    public AskDataItem (String[] data, Databases db, boolean isAddOrRemove) {
        this.data = data;
        this.db = db;
        this.isAddOrRemove = isAddOrRemove;
    }
}
