package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.applyTheme;
import static com.codersanx.splitcost.utils.Utils.getPrefix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.codersanx.splitcost.databinding.ActivitySettingsBinding;
import com.codersanx.splitcost.settings.ChangePrefix;
import com.codersanx.splitcost.settings.ManageDatabase;
import com.codersanx.splitcost.utils.Databases;

import java.util.ArrayList;
import java.util.List;


public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setResult(RESULT_OK);

        List<String> items = getListOfThemes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.theme_adapter, items);
        adapter.setDropDownViewResource(R.layout.elements_theme_spinner);
        binding.theme.setAdapter(adapter);

        Databases settings = new Databases(this, MAIN_SETTINGS);

        if (settings.get("theme") != null && settings.get("theme").equals("dark")) {
            binding.theme.setSelection(1);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        binding.theme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.set("theme", items.get(position).toLowerCase());
                applyTheme(settings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ActivityResultLauncher<Intent> prefix = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
                result -> {
            if (result.getResultCode() == RESULT_OK) {
                binding.startText.setText(getPrefix(this));
            }
        });

        binding.changePrefix.setOnClickListener( v -> prefix.launch(new Intent(this, ChangePrefix.class)));
        binding.startText.setText(getPrefix(this));

        binding.manageDb.setOnClickListener( v -> startActivity(new Intent(this, ManageDatabase.class)));

        binding.backB.setOnClickListener( v -> finish());

        String isChangeTime = new Databases(this, MAIN_SETTINGS).get("time");
        binding.changeTime.setChecked(isChangeTime != null && isChangeTime.equals(TRUE));
        binding.changeTime.setOnCheckedChangeListener((buttonView, isChecked) -> new Databases(this, MAIN_SETTINGS).set("time", isChecked ? TRUE : FALSE));

        binding.githubAnready.setOnClickListener( v -> {
            Uri uri = Uri.parse("https://github.com/Anready");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.githubMarco.setOnClickListener( v -> {
            Uri uri = Uri.parse("https://github.com/marco-tuzza");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.githubCode.setOnClickListener( v -> {
            Uri uri = Uri.parse("https://github.com/Anready/SplitCost");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.instagramSonnya.setOnClickListener( v -> {
            Uri uri = Uri.parse("https://www.instagram.com/ky_sonnya");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.discord.setOnClickListener( v -> {
            Uri uri = Uri.parse("https://discord.com/invite/hXcJKFybvD");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    private List<String> getListOfThemes() {
        List<String> themes = new ArrayList<>();
        themes.add("White");
        themes.add("Dark");
        return themes;
    }
}