package com.codersanx.splitcost.add;

import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.LAST_CATEGORY;
import static com.codersanx.splitcost.utils.Constants.TRUE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.codersanx.splitcost.R;
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

public class DuplicateMethods extends Expense{
    private final Activity context;
    private final Databases category, db, settings;
    private final Spinner categoryE;
    private final Button back, deleteCategory, save;
    private final TextView dateE, timeE;
    private final EditText amountE;

    public DuplicateMethods(Activity context,  Databases category, Databases db,
                            Databases settings, Spinner categoryE, Button back,
                            Button deleteCategory, Button save, TextView dateE,
                            TextView timeE, EditText amountE
    ) {
        this.context = context;
        this.category = category;
        this.db = db;
        this.settings = settings;
        this.categoryE = categoryE;
        this.back = back;
        this.deleteCategory = deleteCategory;
        this.save = save;
        this.dateE = dateE;
        this.timeE = timeE;
        this.amountE = amountE;
    }
    
    void setObjects() {
        setCategoryE();

        back.setOnClickListener( v -> {
            settings.set(LAST_CATEGORY, categoryE.getSelectedItem().toString());
            context.finish();
        });

        dateE.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        timeE.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));

        dateE.setOnClickListener( v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        dateE.setText(selectedDate);
                    }, year, month, dayOfMonth);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        timeE.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, selectedHour, selectedMinute) -> {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d",
                                selectedHour, selectedMinute);
                        timeE.setText(selectedTime);
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        amountE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1 && charSequence.charAt(0) == '0' && charSequence.charAt(1) != '.') {
                    String text = String.valueOf(charSequence);
                    amountE.setText(text.substring(1));
                    amountE.setSelection(0);
                }

                if (charSequence.length() > 0 && charSequence.charAt(0) == '.') {
                    amountE.setText("0.");
                    amountE.setSelection(2);
                }

                if(charSequence.length() > 8 && charSequence.charAt(8) != '.'){
                    amountE.setText(charSequence.subSequence(0, charSequence.length() - 1));
                    amountE.setSelection(8);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                int commaIndex = text.indexOf('.');

                if (commaIndex >= 0 && text.length() - commaIndex > 3) {
                    String newText = text.substring(0, commaIndex + 3);
                    amountE.setText(newText);
                    amountE.setSelection(newText.length());
                }
            }
        });

        deleteCategory.setOnClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.deleteCategory));
            builder.setMessage(context.getResources().getString(R.string.descriptionOfDeleteCategory));

            builder.setPositiveButton(context.getResources().getString(R.string.delete), (dialog, which) -> {
                String categoryToDelete = categoryE.getSelectedItem().toString();

                for (Map.Entry<String, String> entry : db.readAll().entrySet()) {
                    if (entry.getKey().split("@")[1].equals(categoryToDelete)) {
                        category.set(categoryE.getSelectedItem().toString(), FALSE);
                        return;
                    }
                }

                category.delete(categoryToDelete);
                setCategoryE();
            });

            builder.setNegativeButton(context.getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());

            builder.show();
        });

        save.setOnClickListener( v -> {
            String userInput = amountE.getText().toString();
            if(userInput.isEmpty() || userInput.equals("0.0") || userInput.equals("0.") || userInput.equals("0.00") || userInput.equals("0") || categoryE.getSelectedItem().toString().equals("New Category") || userInput.endsWith(".")){
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(userInput.indexOf('.') != -1){
                if(userInput.indexOf('.') + 3 > userInput.length()){
                    userInput = userInput + "0";
                }

                int commaPos = userInput.indexOf(".");
                if(userInput.charAt(commaPos + 1) == '0' && userInput.charAt(commaPos + 2) == '0' || userInput.charAt(commaPos + 1) == '0' && userInput.charAt(userInput.length() - 1) == '0'){
                    userInput = userInput.substring(0, commaPos);
                }
            }

            String seconds = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
            if(seconds.length() == 1){
                seconds = "0" + seconds;
            }

            db.set(dateE.getText().toString() + " " + timeE.getText().toString() + ":" + seconds + "@" + categoryE.getSelectedItem().toString(), userInput);
            settings.set(LAST_CATEGORY, categoryE.getSelectedItem().toString());
            context.setResult(RESULT_OK);
            context.finish();
        });
    }

    private void setCategoryE() {
        String[] categories = getCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryE.setAdapter(adapter);
        categoryE.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);

        int id = Arrays.asList(getCategories()).indexOf(settings.get(LAST_CATEGORY));
        if (id == -1) id = 0;

        categoryE.setSelection(id);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.newCategory));
        builder.setMessage(context.getResources().getString(R.string.descriptionOfNewCategory));

        final EditText input = new EditText(context);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(17);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.contains(" ")) {
                    int commaIndex = text.indexOf(' ');
                    String newText = text.substring(0, commaIndex) + text.substring(commaIndex + 1);
                    input.setText(newText);
                    input.setSelection(commaIndex);
                } else if(text.contains("@")) {
                    int commaIndex = text.indexOf('@');
                    String newText = text.substring(0, commaIndex) + text.substring(commaIndex + 1);
                    input.setText(newText);
                    input.setSelection(commaIndex);
                }
            }
        });

        input.setFilters(filters);
        builder.setView(input);

        builder.setPositiveButton(context.getResources().getString(R.string.create), (dialog, which) -> {
            String inputText = input.getText().toString();
            if(Arrays.asList(getCategories()).contains(inputText)) {
                Toast.makeText(context, context.getResources().getText(R.string.categotyAlreadyExist), Toast.LENGTH_SHORT).show();
                return;
            }

            if(inputText.isEmpty()) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                return;
            }

            settings.set(LAST_CATEGORY, inputText);
            createNewCategory(inputText);
        });

        builder.setNegativeButton(context.getResources().getString(R.string.cancel), (dialog, which) -> {
            categoryE.setSelection(Arrays.asList(getCategories()).indexOf(settings.get(LAST_CATEGORY)));
            dialog.cancel();
        });

        builder.setCancelable(false);

        return builder;
    }

    private void createNewCategory(String name) {
        category.set(name, TRUE);
        setCategoryE();
    }
}
