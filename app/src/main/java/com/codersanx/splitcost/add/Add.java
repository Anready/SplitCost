package com.codersanx.splitcost.add;

import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.LAST_CATEGORY;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.TRUE;
import static com.codersanx.splitcost.utils.Utils.currentDb;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.add.calculator.Calculator;
import com.codersanx.splitcost.databinding.ActivityAddBinding;
import com.codersanx.splitcost.utils.Databases;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Add extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityAddBinding binding;
    private Databases category, db, settings;
    private boolean isExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        isExpenses = intent.getBooleanExtra("isExpense", true);

        initVariables();

        ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String sum = data.getStringExtra("sum");
                            binding.amountE.setText(sum);
                            binding.amountE.setSelection(sum.length());
                        }
                    }
                }
        );

        setCategoryE();
        binding.categoryE.setOnItemSelectedListener(this);

        binding.back.setOnClickListener(v -> {
            settings.set(LAST_CATEGORY, binding.categoryE.getSelectedItem().toString());
            finish();
        });

        binding.dateTimeE.setFocusable(false);
        binding.dateTimeE.setFocusableInTouchMode(false);

        binding.dateTimeE.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date()));

        binding.dateTimeE.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // After selecting date, initiate TimePickerDialog
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                (view1, selectedHour, selectedMinute) -> {
                                    // Combine selected date and time
                                    selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                                    selectedDate.set(Calendar.MINUTE, selectedMinute);

                                    SimpleDateFormat combinedFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                                    binding.dateTimeE.setText(combinedFormat.format(selectedDate.getTime()));
                                }, hour, minute, true);

                        timePickerDialog.show();
                    }, year, month, dayOfMonth);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        binding.amountE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1 && charSequence.charAt(0) == '0' && charSequence.charAt(1) != '.') {
                    String text = String.valueOf(charSequence);
                    binding.amountE.setText(text.substring(1));
                    binding.amountE.setSelection(1);
                }

                if (charSequence.length() > 0 && charSequence.charAt(0) == '.') {
                    binding.amountE.setText("0.");
                    binding.amountE.setSelection(2);
                }

                if (charSequence.length() > 8 && charSequence.charAt(8) != '.') {
                    binding.amountE.setText(charSequence.subSequence(0, charSequence.length() - 1));
                    binding.amountE.setSelection(8);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                int commaIndex = text.indexOf('.');

                if (commaIndex >= 0 && text.length() - commaIndex > 3) {
                    String newText = text.substring(0, commaIndex + 3);
                    binding.amountE.setText(newText);
                    binding.amountE.setSelection(newText.length());
                }
            }
        });

        binding.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.deleteCategory));
            builder.setMessage(getResources().getString(R.string.descriptionOfDeleteCategory));

            builder.setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
                String categoryToDelete = binding.categoryE.getSelectedItem().toString();

                for (Map.Entry<String, String> entry : db.readAll().entrySet()) {
                    if (entry.getKey().split("@")[1].equals(categoryToDelete)) {
                        category.set(binding.categoryE.getSelectedItem().toString(), FALSE);
                        return;
                    }
                }

                category.delete(categoryToDelete);
                setCategoryE();
            });

            builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());

            builder.show();
        });

        binding.save.setOnClickListener(v -> {
            String userInput = binding.amountE.getText().toString();
            if (userInput.isEmpty() || userInput.equals("0.0") || userInput.equals("0.") || userInput.equals("0.00") || userInput.equals("0") || binding.categoryE.getSelectedItem().toString().equals("New Category") || userInput.endsWith(".")) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userInput.indexOf('.') != -1) {
                if (userInput.indexOf('.') + 3 > userInput.length()) {
                    userInput = userInput + "0";
                }

                int commaPos = userInput.indexOf(".");
                if (userInput.charAt(commaPos + 1) == '0' && userInput.charAt(commaPos + 2) == '0' || userInput.charAt(commaPos + 1) == '0' && userInput.charAt(userInput.length() - 1) == '0') {
                    userInput = userInput.substring(0, commaPos);
                }
            }

            String seconds = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
            if (seconds.length() == 1) {
                seconds = "0" + seconds;
            }

            db.set(binding.dateTimeE.getText().toString() + ":" + seconds + "@" + binding.categoryE.getSelectedItem().toString(), userInput);
            settings.set(LAST_CATEGORY, binding.categoryE.getSelectedItem().toString());
            setResult(RESULT_OK);
            finish();
        });

        binding.calculator.setOnClickListener(v -> addLauncher.launch(new Intent(this, Calculator.class)));
    }

    private void initVariables() {
        if (isExpenses) {
            category = new Databases(this, currentDb(this) + CATEGORY + EXPENSES);
            db = new Databases(this, currentDb(this) + EXPENSES);

            binding.titleTextView.setText(getResources().getText(R.string.add_expense));
            binding.save.setText(getResources().getText(R.string.add_expense));
        } else {
            category = new Databases(this, currentDb(this) + CATEGORY + INCOMES);
            db = new Databases(this, currentDb(this) + INCOMES);

            binding.titleTextView.setText(getResources().getText(R.string.add_income));
            binding.save.setText(getResources().getText(R.string.add_income));
        }

        settings = new Databases(this, currentDb(this) + MAIN_SETTINGS);
    }

    private void setCategoryE() {
        String[] categories = getCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categoryE.setAdapter(adapter);

        int id = Arrays.asList(getCategories()).indexOf(settings.get(LAST_CATEGORY));
        if (id == -1) id = 0;

        binding.categoryE.setSelection(id);
    }

    private String[] getCategories() {
        List<String> itemList = new ArrayList<>();

        for (Map.Entry<String, String> entry : category.readAll().entrySet()) {
            if (entry.getValue().equals(TRUE)) {
                itemList.add(entry.getKey());
            }
        }

        Collections.sort(itemList);

        itemList.add("New category");
        return itemList.toArray(new String[0]);
    }

    @NonNull
    AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.newCategory));
        builder.setMessage(getResources().getString(R.string.descriptionOfNewCategory));

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
        builder.setView(input);

        builder.setPositiveButton(getResources().getString(R.string.create), (dialog, which) -> {
            String inputText = input.getText().toString();
            if (Arrays.asList(getCategories()).contains(inputText)) {
                Toast.makeText(this, getResources().getText(R.string.categoryAlreadyExist), Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputText.isEmpty()) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            settings.set(LAST_CATEGORY, inputText);
            createNewCategory(inputText);
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
            binding.categoryE.setSelection(Arrays.asList(getCategories()).indexOf(settings.get(LAST_CATEGORY)));
            dialog.cancel();
        });

        builder.setCancelable(false);

        return builder;
    }

    private void createNewCategory(String name) {
        category.set(name, TRUE);
        setCategoryE();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String) parent.getItemAtPosition(position);

        if (selectedItem.equals("New category")) {
            getBuilder().show();
            return;
        }

        settings.set(LAST_CATEGORY, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}