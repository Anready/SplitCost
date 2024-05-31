package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Utils.currentDb;
import static com.codersanx.splitcost.utils.Utils.initApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codersanx.splitcost.databinding.ActivityMainBinding;
import com.codersanx.splitcost.utils.Databases;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String currentDb;
    private Databases expenses, incomes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initApp(this);
        initVariables();

        binding.currentDB.setText(currentDb);
        binding.perAllDayAddE.setText(perDay(true));
        binding.perAllDayMinusE.setText(perDay(false));
    }

    private String perDay(boolean isExpenses) {
        BigInteger total = BigInteger.ZERO;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Map<String, String> allData;
        if (isExpenses) {
            allData = expenses.readAll();
        } else {
            allData = incomes.readAll();
        }

        for (Map.Entry<String, String> entry : allData.entrySet()) {
            try {
                Date currentDate = formatter.parse(String.valueOf(new Date()));
                Date date1 = formatter.parse(entry.getKey().split("@")[0]);

                int comparison = date1.compareTo(currentDate);
                if (comparison == 0) {
                    total = total.add(new BigInteger(entry.getValue()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return String.valueOf(total);
    }

    //TODO: Create a perAll() setter

    private void initVariables() {
        currentDb = currentDb(this);
        expenses = new Databases(this, currentDb + "Expenses");
        incomes = new Databases(this, currentDb + "Incomes");
    }
}