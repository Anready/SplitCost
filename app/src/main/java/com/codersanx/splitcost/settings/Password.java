package com.codersanx.splitcost.settings;

import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.PASSWORD;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.databinding.ActivityPasswordBinding;
import com.codersanx.splitcost.utils.Databases;

public class Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPasswordBinding binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Databases settings = new Databases(this, MAIN_SETTINGS);
        if (settings.get(PASSWORD) != null) binding.deletePassword.setVisibility(View.VISIBLE);

        binding.back.setOnClickListener(v -> finish());
        binding.viewPassword.setOnClickListener(v -> {
            if (binding.password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                binding.password.setTransformationMethod(null);
            } else {
                binding.password.setTransformationMethod(new PasswordTransformationMethod());
            }

            binding.password.setSelection(binding.password.getText().length());
        });

        binding.viewRepeatPassword.setOnClickListener(v -> {
            if (binding.repeatPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                binding.repeatPassword.setTransformationMethod(null);
            } else {
                binding.repeatPassword.setTransformationMethod(new PasswordTransformationMethod());
            }

            binding.repeatPassword.setSelection(binding.repeatPassword.getText().length());
        });

        binding.save.setOnClickListener(v -> {
            if (binding.password.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.password.getText().toString().equals(binding.repeatPassword.getText().toString())) {
                settings.set(PASSWORD, binding.password.getText().toString());
                Toast.makeText(this, R.string.password_saved, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
            }
        });

        binding.deletePassword.setOnClickListener( v -> {
            settings.delete(PASSWORD);
            setResult(RESULT_OK);
            finish();
        });
    }
}