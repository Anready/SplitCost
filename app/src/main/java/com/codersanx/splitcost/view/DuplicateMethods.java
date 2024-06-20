package com.codersanx.splitcost.view;

import static android.app.Activity.RESULT_OK;

import static com.codersanx.splitcost.utils.Constants.EXPENSES;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.utils.Databases;
import com.codersanx.splitcost.utils.SortItems;
import com.codersanx.splitcost.utils.adapters.Category;
import com.codersanx.splitcost.utils.adapters.ViewCategoriesAdapter;
import com.codersanx.splitcost.view.chart.Chart;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DuplicateMethods {
    private final Activity context;
    private final Databases db;
    private final Spinner modeOfSort;
    private final ImageView backB, chart;
    private final TextView date, startDate, endDate, total;
    private final ListView list;
    private final String nameOfCategory;
    private ActivityResultLauncher<Intent> addLauncher;
    private boolean click = true;

    public DuplicateMethods(Activity context, Databases db, Spinner modeOfSort,
                            ListView list, ImageView backB, ImageView chart, TextView total,
                            TextView date, TextView startDate, TextView endDate, String nameOfCategory

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
        this.nameOfCategory = nameOfCategory;
        this.addLauncher = null;
    }

    void initObjects() {
        List<String> items = new ArrayList<>();
        items.add(context.getResources().getString(R.string.perAll));
        items.add(context.getResources().getString(R.string.perDays));
        items.add(context.getResources().getString(R.string.perMonths));
        items.add(context.getResources().getString(R.string.perYear));
        items.add(context.getResources().getString(R.string.perPeriod));

        if (nameOfCategory == null) {
            items.add(context.getResources().getString(R.string.category));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeOfSort.setAdapter(adapter);

        backB.setOnClickListener(view -> context.finish());

        chart.setOnClickListener(v -> {
            ArrayAdapter<String> getAdapter = (ArrayAdapter<String>) list.getAdapter();
            int count = getAdapter.getCount();

            // Создаем массив для хранения заголовков каждого элемента списка
            String[] titles = new String[count];

            // Получаем заголовок для каждого элемента списка
            for (int i = 0; i < count; i++) {
                View itemView = getAdapter.getView(i, null, list);
                TextView titleTextView = itemView.findViewById(R.id.textViewTitle);
                TextView description = itemView.findViewById(R.id.textViewDescription);
                TextView expense = itemView.findViewById(R.id.textViewExpense);

                if (description.getText().toString().isEmpty()) {
                    titles[i] = titleTextView.getText().toString() + ", " + titleTextView.getText().toString() + ", " + expense.getText().toString();
                    continue;
                }

                titles[i] = titleTextView.getText().toString() + ", " + description.getText().toString().replace("Category: ", "") + ", " + expense.getText().toString();
            }

            Intent intent = new Intent(context, Chart.class);
            intent.putExtra("list_items", titles);
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
            //String selectedItem = (String) parent.getItemAtPosition(position);

            View listItem = view;
            if (listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.category_item_layout, parent, false);

            TextView title = listItem.findViewById(R.id.textViewTitle);
            String titleText = title.getText().toString();

            if (modeOfSort.getSelectedItem().toString().equals(context.getResources().getString(R.string.category))) {
                Intent intent = new Intent(context, CategoryItems.class);
                intent.putExtra("nameOfCategory", titleText);
                intent.putExtra("isExpense", db.getCurrentDbName().contains(EXPENSES));
                addLauncher.launch(intent);
                return;
            }

            TextView description = listItem.findViewById(R.id.textViewDescription);
            String descriptionText = description.getText().toString();

            TextView expense = listItem.findViewById(R.id.textViewExpense);
            String expenseText = expense.getText().toString();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.changeData));
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.enterNewData));

            final EditText input = new EditText(context);

            input.setText(expenseText);
            input.setSelection(expenseText.length());
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

                    db.set(titleText + "@" + descriptionText.replace("Category: ", ""), userInput);

                    setSort(false);

                    click = true;
                    context.setResult(RESULT_OK);
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.delete), (dialog, which) -> {
                db.delete(titleText + "@" + descriptionText.replace("Category: ", ""));
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

    public void setAddLauncher(ActivityResultLauncher<Intent> launcher) {
        this.addLauncher = launcher;
    }

    private String total(String[] finalData) {
        return context.getResources().getString(R.string.total) + " " + totalResult(finalData).toString();
    }

    private BigDecimal totalResult(String[] finalData) {
        BigDecimal result = new BigDecimal("0");

        for (String data : finalData) {
            String[] dataSplit = data.split(", ");

            String count = dataSplit[2];
            BigDecimal bd1 = new BigDecimal(count);
            result = result.add(bd1);
        }

        return result;
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

            setItems(finalData);
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
        } else if (selectedItem.equals(context.getResources().getString(R.string.category))) {
            String[] finalData = getAllData();

            startDate.setText("");
            endDate.setText("");
            date.setText("");

            SortItems comparator = new SortItems();
            Arrays.sort(finalData, comparator);

            setCategories(finalData);
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

        setItems(selectedDates.toArray(new String[0]));
    }


    private String[] getAllData() {
        List<String> itemList = new ArrayList<>();

        for (Map.Entry<String, String> entry : db.readAll().entrySet()) {
            if (nameOfCategory != null && entry.getKey().contains(nameOfCategory)){
                itemList.add(entry.getKey().replace("@", ", ") + ", " + entry.getValue());
                continue;
            }

            if (nameOfCategory != null) {
                continue;
            }

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

        setItems(finalData);
    }

    private Map<String, BigDecimal> getSumOfCategories(String[] items) {
        Map<String, BigDecimal> dictionary = new HashMap<>();

        for (String item : items) {
            String[] splitItem = item.split(", ");

            if (dictionary.containsKey(splitItem[1])) {
                BigDecimal value = dictionary.get(splitItem[1]);
                dictionary.put(splitItem[1], new BigDecimal(splitItem[2]).add(value));
            } else {
                dictionary.put(splitItem[1], new BigDecimal(splitItem[2]));
            }
        }

        return dictionary;
    }

    private void setCategories(String[] finalData) {
        List<Category> items = new ArrayList<>();
        BigDecimal totalV = totalResult(finalData);

        for (Map.Entry<String, BigDecimal> item : getSumOfCategories(finalData).entrySet()) {
            items.add(new Category(item.getKey(), "", item.getValue(), totalV));
        }

        ViewCategoriesAdapter adapter = new ViewCategoriesAdapter(context, items);
        list.setAdapter(adapter);
    }

    private void setItems(String[] finalData) {
        SortItems comparator = new SortItems();
        Arrays.sort(finalData, comparator);

        List<Category> items = new ArrayList<>();
        BigDecimal totalV = totalResult(finalData);

        for (String item : finalData) {
            String[] itemSplit = item.split(", ");
            items.add(new Category(itemSplit[0], "Category: " + itemSplit[1], new BigDecimal(itemSplit[2]), totalV));
        }

        ViewCategoriesAdapter adapter = new ViewCategoriesAdapter(context, items);

        total.setText(total(finalData));
        list.setAdapter(adapter);
    }
}
