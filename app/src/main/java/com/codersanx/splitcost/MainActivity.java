package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Utils.currentDb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.codersanx.splitcost.databinding.ActivityMainBinding;
import com.codersanx.splitcost.utils.Databases;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Databases db = new Databases(this, currentDb(this) + "Minus");

        for (int i = 0; i < 20; i++)
            db.set("ÄllStrings", "Hi");

        String[] allData = db.get("AllStrings").split("\n");
        System.out.println(Arrays.toString(allData));
    }
}