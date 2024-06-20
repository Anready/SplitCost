package com.codersanx.splitcost.view;

import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Utils.currentDb;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityCategoryItemsBinding;
import com.codersanx.splitcost.utils.Databases;

public class CategoryItems extends AppCompatActivity {

    private Databases db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCategoryItemsBinding binding = ActivityCategoryItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String nameOfCategory = intent.getStringExtra("nameOfCategory");
        boolean isExpense = intent.getBooleanExtra("isExpense", true);

        initVariables(isExpense);

        DuplicateMethods dm = new DuplicateMethods(this, db, binding.modeOfSort,
                binding.list, binding.backB, binding.chart, binding.total,
                binding.date, binding.startDate, binding.endDate, nameOfCategory
        );

        dm.initObjects();
    }

    private void initVariables(boolean isExpense) {
        if (isExpense) {
            db = new Databases(this, currentDb(this) + EXPENSES);
        } else {
            db = new Databases(this, currentDb(this) + INCOMES);
        }
    }

}