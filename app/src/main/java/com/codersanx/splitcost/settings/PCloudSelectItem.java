package com.codersanx.splitcost.settings;

import static com.codersanx.splitcost.utils.adapters.FileSelectAdapter.getObjectsByFolderId;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityPcloudSelectItemBinding;

public class PCloudSelectItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPcloudSelectItemBinding binding = ActivityPcloudSelectItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getObjectsByFolderId(this, true);
    }
}