package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.PASSWORD;
import static com.codersanx.splitcost.utils.Utils.initApp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codersanx.splitcost.databinding.ActivityStartPasswordBinding;
import com.codersanx.splitcost.utils.Databases;

public class StartPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.TRANSLATION_Y,
                        0f,
                        -splashScreenView.getHeight()
                );
                slideUp.setInterpolator(new AnticipateInterpolator());
                slideUp.setDuration(600L);

                slideUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                    }
                });

                slideUp.start();
            });
        }

        ActivityStartPasswordBinding binding = ActivityStartPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initApp(this);

        Databases settings = new Databases(this, MAIN_SETTINGS);

        if (settings.get(PASSWORD) == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        binding.password.setVisibility(View.VISIBLE);
        binding.logo.setVisibility(View.VISIBLE);
        binding.viewPassword.setVisibility(View.VISIBLE);
        binding.login.setVisibility(View.VISIBLE);

        binding.viewPassword.setOnClickListener(v -> {
            if (binding.password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                binding.password.setTransformationMethod(null);
            } else {
                binding.password.setTransformationMethod(new PasswordTransformationMethod());
            }

            binding.password.setSelection(binding.password.getText().length());
        });

        binding.login.setOnClickListener( v -> {
            if (settings.get(PASSWORD).equals(binding.password.getText().toString())) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, getString(R.string.wrongPassword), Toast.LENGTH_SHORT).show();
            }
        });
    }
}