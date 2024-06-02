package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Utils.currentDb;
import static com.codersanx.splitcost.utils.Utils.initApp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.codersanx.splitcost.add.Expense;
import com.codersanx.splitcost.add.Income;
import com.codersanx.splitcost.databinding.ActivityMainBinding;
import com.codersanx.splitcost.utils.Databases;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String currentDb;
    private Databases expenses, incomes;
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
                        expensesPerAll = perAll(true);
                        incomesPerAll = perAll(false);
                        setText();
                    }
                }
        );

        binding.expenseAdd.setOnClickListener(v -> addLauncher.launch(new Intent(this, Expense.class)));
        binding.incomeAdd.setOnClickListener(v -> addLauncher.launch(new Intent(this, Income.class)));
    }

    private void setText() {
        binding.currentDB.setText(currentDb);
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
            } catch (ParseException e) {
                e.printStackTrace();
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