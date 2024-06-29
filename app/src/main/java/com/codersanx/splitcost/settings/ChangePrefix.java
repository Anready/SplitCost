package com.codersanx.splitcost.settings;

import static com.codersanx.splitcost.utils.Constants.ALL_PREFIX;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.PREFIX;
import static com.codersanx.splitcost.utils.Utils.currentDb;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.databinding.ActivityChangePrefixBinding;
import com.codersanx.splitcost.utils.Databases;

public class ChangePrefix extends AppCompatActivity {

    ActivityChangePrefixBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePrefixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ALL_PREFIX);
        binding.list.setAdapter(adapter);

        binding.list.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            new Databases(this, currentDb(this) + MAIN_SETTINGS).set(PREFIX, selectedItem.split(", ")[1]);
            Toast.makeText(this, getResources().getText(R.string.currency_changed) + selectedItem.split(", ")[1], Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.backB.setOnClickListener( v -> finish());
    }
}