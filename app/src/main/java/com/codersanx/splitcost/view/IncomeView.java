package com.codersanx.splitcost.view;

import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Utils.currentDb;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityIncomeViewBinding;
import com.codersanx.splitcost.utils.Databases;

public class IncomeView extends AppCompatActivity {
    private Databases db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityIncomeViewBinding binding = ActivityIncomeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initVariables();

        DuplicateMethods dm = new DuplicateMethods(this, db, binding.modeOfSort,
                binding.list, binding.backB, binding.chart, binding.total,
                binding.date, binding.startDate, binding.endDate
        );

        dm.initObjects();
    }

    private void initVariables() {
        db = new Databases(this, currentDb(this) + INCOMES);
    }
}