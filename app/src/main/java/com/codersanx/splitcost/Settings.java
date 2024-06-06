package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.ALL_DATABASES;
import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.currentDb;
import static com.codersanx.splitcost.utils.Utils.getAllDb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivitySettingsBinding;
import com.codersanx.splitcost.utils.Databases;

import java.io.File;


public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    private Databases allDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initVariables();
        initObjects();
    }

    private void initObjects() {
        binding.listOfDb.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.deleteDb));
            alertDialogBuilder.setMessage(getResources().getString(R.string.deleteDbDescription));

            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
                if(allDb.readAll().size() < 2) {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentDb(this).equals(selectedItem)) {
                    Toast.makeText(this, getResources().getText(R.string.changeDbBeforeDelete), Toast.LENGTH_SHORT).show();
                    return;
                }

                setResult(RESULT_OK);
                allDb.delete(selectedItem);

                deleteAll(selectedItem + INCOMES);
                deleteAll(selectedItem + EXPENSES);
                deleteAll(selectedItem + CATEGORY + INCOMES);
                deleteAll(selectedItem + CATEGORY + EXPENSES);
                deleteAll(selectedItem + MAIN_SETTINGS);

                initVariables();
            });

            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        binding.addNew.setOnClickListener( v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.createDb));
            alertDialogBuilder.setMessage(getResources().getString(R.string.descriptionCreateDb));

            final EditText input = new EditText(this);
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(17);

            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = editable.toString();
                    if (text.contains(" ")) {
                        int commaIndex = text.indexOf(' ');
                        String newText = text.substring(0, commaIndex) + text.substring(commaIndex + 1);
                        input.setText(newText);
                        input.setSelection(commaIndex);
                    } else if (text.contains("@")) {
                        int commaIndex = text.indexOf('@');
                        String newText = text.substring(0, commaIndex) + text.substring(commaIndex + 1);
                        input.setText(newText);
                        input.setSelection(commaIndex);
                    }
                }
            });

            input.setFilters(filters);
            alertDialogBuilder.setView(input);

            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.create), (dialog, which) -> {
                String userInput = input.getText().toString();
                if (!userInput.isEmpty()) {
                    allDb.set(userInput, TRUE);
                    new Databases(this, userInput + CATEGORY + EXPENSES).set("Products", TRUE);
                    new Databases(this, userInput + CATEGORY + INCOMES).set("Salary", TRUE);

                    initVariables();
                    setResult(RESULT_OK);
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        binding.backB.setOnClickListener( v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(Settings.this, MainActivity.class));
                finish();
            }
        });
    }

    private void deleteAll(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            deleteSharedPreferences(s);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(s, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            File dir = new File(getFilesDir().getParent() + "/shared_prefs/");
            File file = new File(dir, s + ".xml");
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void initVariables() {
        allDb = new Databases(this, ALL_DATABASES);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getAllDb(this));
        binding.listOfDb.setAdapter(adapter);
    }
}