package com.codersanx.splitcost.view;

import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Utils.currentDb;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityExpenseViewBinding;
import com.codersanx.splitcost.utils.Databases;

public class ExpenseView extends AppCompatActivity {
    private Databases db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityExpenseViewBinding binding = ActivityExpenseViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initVariables();

        DuplicateMethods dm = new DuplicateMethods(this, db, binding.modeOfSort,
                binding.list, binding.backB, binding.chart, binding.total,
                binding.date, binding.startDate, binding.endDate, null
        );

        dm.initObjects();

        ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        dm.setSort(false);
                    }
                }
        );

        dm.setAddLauncher(addLauncher);
    }

    private void initVariables() {
        db = new Databases(this, currentDb(this) + EXPENSES);
    }
}