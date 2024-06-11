package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.ALL_DATABASES;
import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.currentDb;
import static com.codersanx.splitcost.utils.Utils.getAllDb;
import static com.codersanx.splitcost.utils.Utils.renameFile;
import static com.codersanx.splitcost.utils.Zip.extractZip;
import static com.codersanx.splitcost.utils.Zip.packFile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.codersanx.splitcost.databinding.ActivitySettingsBinding;
import com.codersanx.splitcost.utils.Databases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    private Databases allDb;
    private boolean clickAllow = true;
    private ActivityResultLauncher<Intent> sendFileLauncher, getFile;
    private String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sendFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    File fileToDelete = new File(getFilesDir(), newName);
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                });

        getFile = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        copyFile(result.getData().getData());
                    }
                });


        initVariables();
        initObjects();
    }

    private void initObjects() {
        binding.importDb.setOnClickListener( v -> importDb());
        binding.listOfDb.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.deleteDb));
            alertDialogBuilder.setMessage(getResources().getString(R.string.deleteDbDescription));

            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.delete), (dialog, which) -> {
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

            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.exportDb), (dialog, which) -> {
                clickAllow = false;
                Toast.makeText(this, getResources().getString(R.string.pleaseWait), Toast.LENGTH_SHORT).show();
                exportDb(selectedItem);

                new Handler().postDelayed(() -> clickAllow = true, 10000);
            });

            if (clickAllow) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
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
                    if (allDb.get(userInput) != null) {
                        Toast.makeText(this, getResources().getString(R.string.dbAlreadyExist), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allDb.set(userInput, TRUE);
                    new Databases(this, userInput + CATEGORY + EXPENSES).set("Products", TRUE);
                    new Databases(this, userInput + CATEGORY + INCOMES).set("Salary", TRUE);

                    initVariables();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(requestCode == APP_STORAGE_ACCESS_REQUEST_CODE && Environment.isExternalStorageManager()){
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getFile.launch(Intent.createChooser(intent, "Choose SCE file"));
            }
        } else {
            if(requestCode == APP_STORAGE_ACCESS_REQUEST_CODE && ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getFile.launch(Intent.createChooser(intent, "Choose SCE file"));
            }
        }
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

    private void exportDb(String database) {
        packFile(this, database);
        String filePath = database + ".zip";
        String newName = database + ".sce";

        File oldFile = new File(getFilesDir(), filePath);
        renameFile(oldFile, newName);

        this.newName = newName;

        File file = new File(getFilesDir(), newName);
        Uri fileUri = FileProvider.getUriForFile(this, "com.codersanx.splitcost.fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        sendFileLauncher.launch(Intent.createChooser(intent, "Send file"));
    }
    final static int APP_STORAGE_ACCESS_REQUEST_CODE = 501;
    private void importDb() {
        if (!checkPermission()) {
            requestPermission();
            return;
        }

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getFile.launch(Intent.createChooser(intent, "Choose SCE file"));
    }

    private void initVariables() {
        allDb = new Databases(this, ALL_DATABASES);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getAllDb(this));
        binding.listOfDb.setAdapter(adapter);
    }

    private void copyFileToInternalStorage(Uri uri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Открываем InputStream для чтения данных из URI
            inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return;
            }

            // Получаем имя файла из URI
            String fileName = getFileNameFromUri(uri);
            if (fileName == null) {
                return;
            }

            File outputFile = new File(getFilesDir(), fileName);
            outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException ignored) {
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String path = uri.getLastPathSegment();
        if (path != null) {
            int index = path.lastIndexOf('/');
            if (index != -1) {
                return path.substring(index + 1);
            }
            return path;
        }
        return null;
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;
        }
    }


    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, APP_STORAGE_ACCESS_REQUEST_CODE
            );
        }
    }

    private void copyFile(Uri selectedFileUri) {
        copyFileToInternalStorage(selectedFileUri);

        Toast.makeText(this, getResources().getString(R.string.pleaseWait), Toast.LENGTH_SHORT).show();
        extractZip(this, getFileNameFromUri(selectedFileUri));
        initVariables();
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }
}