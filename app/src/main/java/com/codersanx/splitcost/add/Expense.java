package com.codersanx.splitcost.add;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityExpenseBinding;

public class Expense extends AppCompatActivity {

    private ActivityExpenseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setResult(RESULT_OK);
    }
}