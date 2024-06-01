package com.codersanx.splitcost.add;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityIncomeBinding;

public class Income extends AppCompatActivity {

    private ActivityIncomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIncomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setResult(RESULT_OK);
    }
}