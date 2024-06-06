package com.codersanx.splitcost.add.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.databinding.ActivityCalculatorBinding;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Calculator extends AppCompatActivity {

    ActivityCalculatorBinding binding;
    public String $view = "";
    public int pov = 1, u = 0, t = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        binding = ActivityCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.enter.setMovementMethod(new ScrollingMovementMethod());

        binding.done.setOnClickListener(view -> {
            if (u == 1){
                BigDecimal bd1 = new BigDecimal($view);
                BigDecimal bd2 = new BigDecimal("9999999.99");
                BigDecimal bd3 = new BigDecimal("1");

                int res = bd1.compareTo(bd2);
                int res1 = bd1.compareTo(bd3);

                if(res < 0 && res1 > 0){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("sum", binding.enter.getText().toString());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }else{
                    Toast.makeText(Calculator.this, getResources().getString(R.string.toLargeOrSmall), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.cancel.setOnClickListener(view -> finish());

        binding.zero.setOnClickListener(view -> number("0"));
        binding.one.setOnClickListener(view -> number("1"));
        binding.two.setOnClickListener(view -> number("2"));
        binding.three.setOnClickListener(view -> number("3"));
        binding.four.setOnClickListener(view -> number("4"));
        binding.five.setOnClickListener(view -> number("5"));
        binding.six.setOnClickListener(view -> number("6"));
        binding.seven.setOnClickListener(view -> number("7"));
        binding.eight.setOnClickListener(view -> number("8"));
        binding.nine.setOnClickListener(view -> number("9"));
        binding.multiply.setOnClickListener(v -> work("*"));
        binding.plus.setOnClickListener(v -> work("+"));
        binding.minus.setOnClickListener(v -> work("-"));
        binding.divide.setOnClickListener(v -> work("/"));

        binding.dot.setOnClickListener( v -> {
            if(pov == 0 && t == 0){
                pov = 1;
                $view += ".";
                t = 1;
                binding.enter.setText($view);
            }
        });

        binding.equals.setOnClickListener( v -> {
            if(pov != 0){
                $view = $view.substring(0, $view.length() - 1);
            }

            t = 1;
            u = 1;
            try {
                Expression e = new ExpressionBuilder($view).build();
                BigDecimal result = new BigDecimal(String.valueOf(e.evaluate()));
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(2);
                $view = df.format(result).replace(',', '.');
                binding.enter.setText($view);
            } catch (Exception ignored) {
            }
        });

        binding.AC.setOnClickListener( v -> {
            pov = 1;
            u = 0;
            t = 0;
            $view = "";
            binding.enter.setText("");
        });

        binding.Clean.setOnClickListener( v -> {
            pov = 0;
            u = 0;

            if($view.isEmpty()){
                return;
            }

            if($view.charAt($view.length() - 1) == '.'){
                t = 0;
            }

            $view = $view.substring(0, $view.length() - 1);

            if($view.isEmpty()){
                $view = "";
                binding.enter.setText($view);
                return;
            }

            if($view.charAt($view.length() - 1) == '/' || $view.charAt($view.length() - 1) == '-' || $view.charAt($view.length() - 1) == '+' || $view.charAt($view.length() - 1) == '*'){
                pov = 1;
            }

            binding.enter.setText($view);
        });
    }

    private void work(String s) {
        if (pov != 0) {
            $view = $view.substring(0, $view.length() - 1);
        }
        $view += s;
        t = 0;
        u = 0;
        binding.enter.setText($view);
        pov = 1;
    }

    private void number(String number) {
        pov = 0;
        u = 0;
        $view += number;
        binding.enter.setText($view);
    }
}