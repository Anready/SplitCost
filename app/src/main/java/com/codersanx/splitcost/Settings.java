package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.PASSWORD;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.applyTheme;
import static com.codersanx.splitcost.utils.Utils.getPrefix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.codersanx.splitcost.databinding.ActivitySettingsBinding;
import com.codersanx.splitcost.settings.ChangePrefix;
import com.codersanx.splitcost.settings.ManageDatabase;
import com.codersanx.splitcost.settings.Password;
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

        if (settings.get(PASSWORD) != null) {
            binding.setPasswordIcon.setImageResource(R.drawable.ic_lock);
        } else {
            binding.setPasswordIcon.setImageResource(R.drawable.ic_unlock);
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

                if (settings.get(PASSWORD) != null) {
                    binding.setPasswordIcon.setImageResource(R.drawable.ic_lock);
                } else {
                    binding.setPasswordIcon.setImageResource(R.drawable.ic_unlock);
                }
            }
        });

        binding.changePrefix.setOnClickListener( v -> prefix.launch(new Intent(this, ChangePrefix.class)));
        binding.startText.setText(getPrefix(this));

        binding.manageDb.setOnClickListener( v -> startActivity(new Intent(this, ManageDatabase.class)));

        binding.backB.setOnClickListener( v -> finish());

        String isChangeTime = new Databases(this, MAIN_SETTINGS).get("time");
        binding.changeTime.setChecked(isChangeTime != null && isChangeTime.equals(TRUE));
        binding.changeTime.setOnCheckedChangeListener((buttonView, isChecked) -> new Databases(this, MAIN_SETTINGS).set("time", isChecked ? TRUE : FALSE));

        binding.passwordHolder.setOnClickListener( v -> {
            if (settings.get(PASSWORD) != null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.RoundedDialog);
                alertDialogBuilder.setTitle(getResources().getString(R.string.password));
                alertDialogBuilder.setMessage(getResources().getString(R.string.enterPasswordBeforeEditing));

                final EditText input = new EditText(this);

                int marginHorizontal = 40;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(marginHorizontal, 20, marginHorizontal, 0);
                input.setLayoutParams(params);

                LinearLayout container = new LinearLayout(this);
                container.setOrientation(LinearLayout.VERTICAL);
                container.addView(input);

                alertDialogBuilder.setView(container);

                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.change), (dialog, which) -> {
                    String userInput = input.getText().toString();
                    if (settings.get(PASSWORD).equals(userInput)) {
                        prefix.launch(new Intent(this, Password.class));
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.wrongPassword), Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                prefix.launch(new Intent(this, Password.class));
            }
        });

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