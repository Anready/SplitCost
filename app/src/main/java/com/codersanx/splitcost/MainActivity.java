package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.CURRENT_DB;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Utils.currentDb;
import static com.codersanx.splitcost.utils.Utils.getAllDb;
import static com.codersanx.splitcost.utils.Utils.initApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.add.Expense;
import com.codersanx.splitcost.add.Income;
import com.codersanx.splitcost.databinding.ActivityMainBinding;
import com.codersanx.splitcost.utils.Databases;
import com.codersanx.splitcost.view.ExpenseView;
import com.codersanx.splitcost.view.IncomeView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String currentDb;
    private Databases expenses, incomes, db;
    private String expensesPerAll, incomesPerAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initApp(this);
        initVariables();
        initObjects();
    }

    private void initVariables() {
        currentDb = currentDb(this);
        db = new Databases(this, MAIN_SETTINGS);
        expenses = new Databases(this, currentDb + EXPENSES);
        incomes = new Databases(this, currentDb + INCOMES);
        expensesPerAll = perAll(true);
        incomesPerAll = perAll(false);
    }

    private void initObjects() {
        setText();

        ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        initVariables();
                        setText();
                    }
                }
        );

        binding.expenseAdd.setOnClickListener(v -> addLauncher.launch(new Intent(this, Expense.class)));
        binding.incomeAdd.setOnClickListener(v -> addLauncher.launch(new Intent(this, Income.class)));
        binding.expenesView.setOnClickListener(v -> addLauncher.launch(new Intent(this, ExpenseView.class)));
        binding.incomesView.setOnClickListener(v -> addLauncher.launch(new Intent(this, IncomeView.class)));
        binding.settings.setOnClickListener(v -> addLauncher.launch(new Intent(this, Settings.class)));

        binding.currentDb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                db.set(CURRENT_DB, binding.currentDb.getSelectedItem().toString());
                initVariables();
                setText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setText() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getAllDb(this));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.currentDb.setAdapter(adapter);

        int id = getAllDb(this).indexOf(currentDb);
        if (id == -1) id = 0;

        binding.currentDb.setSelection(id);
        binding.perAllDayMinusE.setText(perDay(true));
        binding.perAllDayAddE.setText(perDay(false));
        binding.perAllHistoryAddE.setText(incomesPerAll);
        binding.perAllHistoryMinusE.setText(expensesPerAll);
        binding.balans.setText(getBalance());
    }

    private String perDay(boolean isExpenses) {
        BigDecimal total = BigDecimal.ZERO;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Map<String, String> allData;

        allData = isExpenses ? expenses.readAll() : incomes.readAll();

        // Format the current date
        String currentDateStr = formatter.format(new Date());

        for (Map.Entry<String, String> entry : allData.entrySet()) {
            try {
                Date currentDate = formatter.parse(currentDateStr);
                Date date1 = formatter.parse(entry.getKey().split(" ")[0]);

                int comparison = date1.compareTo(currentDate);
                if (comparison == 0) {
                    total = total.add(new BigDecimal(entry.getValue()));
                }
            } catch (ParseException ignored) {
            }
        }

        return String.valueOf(total);
    }

    private String perAll(boolean isExpenses) {
        BigDecimal total = BigDecimal.ZERO;

        Map<String, String> allData;
        allData = isExpenses ? expenses.readAll() : incomes.readAll();

        for (Map.Entry<String, String> entry : allData.entrySet()) {
            total = total.add(new BigDecimal(entry.getValue()));
        }

        return String.valueOf(total);
    }

    private String getBalance() {
        BigDecimal incomes = new BigDecimal(incomesPerAll);
        BigDecimal expenses = new BigDecimal(expensesPerAll);

        return incomes.subtract(expenses).toString();
    }
}