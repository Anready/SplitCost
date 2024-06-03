package com.codersanx.splitcost.view;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.utils.Databases;
import com.codersanx.splitcost.utils.SortItems;
import com.codersanx.splitcost.view.chart.Chart;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DuplicateMethods {
    private final Activity context;
    private final Databases db;
    private final Spinner modeOfSort;
    private final Button backB, chart;
    private final TextView date, startDate, endDate, total;
    private final ListView list;
    private boolean click = true;

    public DuplicateMethods(Activity context, Databases db, Spinner modeOfSort,
                            ListView list, Button backB, Button chart, TextView total,
                            TextView date, TextView startDate, TextView endDate

    ) {
        this.context = context;
        this.db = db;
        this.modeOfSort = modeOfSort;
        this.list = list;
        this.backB = backB;
        this.chart = chart;
        this.total = total;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    void initObjects() {
        List<String> items = new ArrayList<>();
        items.add(context.getResources().getString(R.string.perAll));
        items.add(context.getResources().getString(R.string.perDays));
        items.add(context.getResources().getString(R.string.perMonths));
        items.add(context.getResources().getString(R.string.perYear));
        items.add(context.getResources().getString(R.string.perPeriod));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeOfSort.setAdapter(adapter);

        backB.setOnClickListener(view -> context.finish());

        chart.setOnClickListener( v -> {
            ArrayAdapter<String> getAdapter = (ArrayAdapter<String>) list.getAdapter();

            int count = getAdapter.getCount();
            String[] itemsFromList = new String[count];
            for (int i = 0; i < count; i++) {
                itemsFromList[i] = getAdapter.getItem(i);
            }

            Intent intent = new Intent(context, Chart.class);
            intent.putExtra("list_items", itemsFromList);
            context.startActivity(intent);
        });

        modeOfSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSort(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        startDate.setOnClickListener(view -> setDate(false));
        endDate.setOnClickListener(view -> setDate(true));

        date.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (datePicker, year13, monthOfYear, dayOfMonth13) -> {
                String selectedItem = modeOfSort.getSelectedItem().toString();
                String date;
                if (selectedItem.equals(context.getResources().getString(R.string.perDays))) {
                    date = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth13, monthOfYear + 1, year13);
                } else if (selectedItem.equals(context.getResources().getString(R.string.perMonths))) {
                    date = String.format(Locale.getDefault(), "%02d-%04d", monthOfYear + 1, year13);
                } else {
                    date = String.format(Locale.getDefault(), "%04d", year13);
                }

                this.date.setText(date);
                setSort(false);
            }, year, month, dayOfMonth);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.changeData));
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.enterNewData));

            final EditText input = new EditText(context);

            String[] count = selectedItem.split(", ");
            input.setText(count[2]);
            input.setSelection(count[2].length());
            alertDialogBuilder.setView(input);

            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 1 && charSequence.charAt(0) == '0' && charSequence.charAt(1) != '.') {
                        String text = String.valueOf(charSequence);
                        input.setText(text.substring(1));
                        input.setSelection(0);
                    }

                    if (charSequence.length() > 0 && charSequence.charAt(0) == '.') {
                        input.setText("0.");
                        input.setSelection(2);
                    }

                    if (charSequence.length() > 8 && charSequence.charAt(8) != '.') {
                        input.setText(charSequence.subSequence(0, charSequence.length() - 1));
                        input.setSelection(8);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = editable.toString();
                    int commaIndex = text.indexOf('.');
                    if (commaIndex >= 0 && text.length() - commaIndex > 3) {
                        String newText = text.substring(0, commaIndex + 3);
                        input.setText(newText);
                        input.setSelection(newText.length());
                    }
                }
            });

            alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.change), (dialog, which) -> {
                String userInput = input.getText().toString(); // Getting the entered value from the text field.
                if (!userInput.isEmpty() && !userInput.equals("0.0") && !userInput.equals("0.") && !userInput.equals("0.00") && !userInput.equals("0") && !userInput.endsWith(".")) {
                    click = false;
                    if (userInput.indexOf('.') != -1) {
                        if (userInput.indexOf('.') + 3 > userInput.length()) {
                            userInput = userInput + "0";
                        }

                        int commaPos = userInput.indexOf(".");
                        if (userInput.charAt(commaPos + 1) == '0' && userInput.charAt(commaPos + 2) == '0' || userInput.charAt(commaPos + 1) == '0' && userInput.charAt(userInput.length() - 1) == '0') {
                            userInput = userInput.substring(0, commaPos);
                        }
                    }

                    String[] splitOsSI = selectedItem.split(", ");
                    db.set(splitOsSI[0] + "@" + splitOsSI[1], userInput);

                    setSort(false);

                    click = true;
                    context.setResult(RESULT_OK);
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.delete), (dialog, which) -> {
                String[] splitOsSI = selectedItem.split(", ");

                db.delete(splitOsSI[0] + "@" + splitOsSI[1]);
                context.setResult(RESULT_OK);

                setSort(false);

                click = true;
            });

            if (click) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void setDate(boolean isEnd) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (datePicker, year1, monthOfYear, dayOfMonth1) -> {
            String date = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth1, monthOfYear + 1, year1);

            if (isEnd) {
                endDate.setText(date);
            } else {
                startDate.setText(date);
            }

            sortByDate();
        }, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String total(String[] finalData) {
        BigDecimal result = new BigDecimal("0");

        for (String data : finalData) {
            String[] dataSplit = data.split(", ");

            String count = dataSplit[2];
            BigDecimal bd1 = new BigDecimal(count);
            result = result.add(bd1);
        }

        return context.getResources().getString(R.string.total) + " " + result.toString();
    }

    public void setSort(boolean isUpdateNeeded) {
        String selectedItem = modeOfSort.getSelectedItem().toString();
        if (selectedItem.equals(context.getResources().getString(R.string.perDays))) {
            setSortByDate("dd-MM-yyyy", isUpdateNeeded);
        } else if (selectedItem.equals(context.getResources().getString(R.string.perMonths))) {
            setSortByDate("MM-yyyy", isUpdateNeeded);
        } else if (selectedItem.equals(context.getResources().getString(R.string.perYear))) {
            setSortByDate("yyyy", isUpdateNeeded);
        } else if (selectedItem.equals(context.getResources().getString(R.string.perAll))) {
            String[] finalData = getAllData();

            startDate.setText("");
            endDate.setText("");
            date.setText("");

            SortItems comparator = new SortItems();
            Arrays.sort(finalData, comparator);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, finalData);

            total.setText(total(finalData));
            list.setAdapter(adapter);
        } else if (selectedItem.equals(context.getResources().getString(R.string.perPeriod))) {
            Date currentDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = dateFormatter.format(currentDate);

            date.setText("");

            if (isUpdateNeeded) {
                startDate.setText(formattedDate);
                endDate.setText(formattedDate);
            }

            sortByDate();
        }
    }

    public void sortByDate() {
        ArrayList<String> selectedDates = new ArrayList<>();
        String startDateString = startDate.getText().toString();
        String endDateString = endDate.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date startDate = null;
        Date endDate = null;

        try {
            startDate = dateFormat.parse(startDateString);
            endDate = dateFormat.parse(endDateString);
        } catch (ParseException ignored) {
        }

        for (String dateString : getAllData()) {
            Date date = null;
            try {
                date = dateFormat.parse(dateString.substring(0, 10));
            } catch (ParseException ignored) {
            }

            if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
                selectedDates.add(dateString);
            }
        }

        String[] selectedDatesArray = selectedDates.toArray(new String[0]);

        SortItems comparator = new SortItems();
        Arrays.sort(selectedDatesArray, comparator);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, selectedDatesArray);

        total.setText(total(selectedDatesArray));
        list.setAdapter(adapter);
    }


    private String[] getAllData() {
        List<String> itemList = new ArrayList<>();

        for (Map.Entry<String, String> entry : db.readAll().entrySet()) {
            itemList.add(entry.getKey().replace("@", ", ") + ", " + entry.getValue());
        }

        return itemList.toArray(new String[0]);
    }

    private void setSortByDate(String type, boolean needUpdate) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(type, Locale.getDefault());

        Date currentDate = new Date();
        String formattedDate = dateFormatter.format(currentDate);

        startDate.setText("");
        endDate.setText("");

        if (needUpdate) {
            date.setText(formattedDate);
        }

        String[] finalData = new String[]{};

        for (String data : getAllData()) {
            try {
                String date = data.substring(0, 10);
                Date dataDate = dateFormatter.parse(date);

                String comparisonDate = needUpdate ? formattedDate : this.date.getText().toString();

                if (dataDate != null && date.contains(comparisonDate)) {
                    String[] newItems = Arrays.copyOf(finalData, finalData.length + 1);
                    newItems[newItems.length - 1] = data;
                    finalData = newItems;
                }
            } catch (ParseException ignored) {
            }
        }

        SortItems comparator = new SortItems();
        Arrays.sort(finalData, comparator);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, finalData);

        total.setText(total(finalData));
        list.setAdapter(adapter);
    }
}
