package com.codersanx.splitcost.add;

import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.LAST_CATEGORY;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Utils.currentDb;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.add.calculator.Calculator;
import com.codersanx.splitcost.databinding.ActivityExpenseBinding;
import com.codersanx.splitcost.utils.Databases;


public class Expense extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Databases category, db, settings;
    private DuplicateMethods dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityExpenseBinding binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initVariables();
        dm = new DuplicateMethods(this, category, db, settings,
                binding.categoryE, binding.back, binding.delete,
                binding.save, binding.dateTimeE, binding.amountE
        );

        dm.setObjects();

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

        binding.calculator.setOnClickListener(v -> addLauncher.launch(new Intent(this, Calculator.class)));
    }

    private void initVariables() {
        category = new Databases(this, currentDb(this) + CATEGORY + EXPENSES);
        db = new Databases(this, currentDb(this) + EXPENSES);
        settings = new Databases(this, currentDb(this) + MAIN_SETTINGS);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String) parent.getItemAtPosition(position);

        if (selectedItem.equals("New category")) {
            dm.getBuilder().show();
            return;
        }

        settings.set(LAST_CATEGORY, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}