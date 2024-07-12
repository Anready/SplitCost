package com.codersanx.splitcost;

import static com.codersanx.splitcost.utils.Constants.CURRENT_DB;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Utils.currentDb;
import static com.codersanx.splitcost.utils.Utils.getAllDb;
import static com.codersanx.splitcost.utils.Utils.getPrefix;
import static com.codersanx.splitcost.utils.Utils.initApp;
import static com.codersanx.splitcost.utils.Utils.isDatabaseOnline;
import static com.codersanx.splitcost.utils.Utils.isInternetAvailable;
import static com.codersanx.splitcost.utils.Utils.synchronizeDb;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.codersanx.splitcost.add.Add;
import com.codersanx.splitcost.databinding.ActivityMainBinding;
import com.codersanx.splitcost.utils.Databases;
import com.codersanx.splitcost.utils.GetUpdate;
import com.codersanx.splitcost.utils.SyncDbOnExit;
import com.codersanx.splitcost.view.ViewData;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GetUpdate.UpdateCallback {

    private ActivityMainBinding binding;
    private String currentDb, prefix;
    private Databases expenses, incomes, db;
    private String expensesPerAll, incomesPerAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (isInternetAvailable(getApplication()) && isDatabaseOnline(this)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.RoundedDialog);
            alertDialogBuilder.setTitle(getResources().getString(R.string.changeData));
            alertDialogBuilder.setMessage(getResources().getString(R.string.enterNewData));
            alertDialogBuilder.setCancelable(false);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            synchronizeDb(this, alertDialog);
        }

        GetUpdate fetchData = new GetUpdate(getResources().getString(R.string.URL_WITH_UPDATES), this, this);
        fetchData.getUpdateInformation();

        initVariables();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, getAllDb(this));
        binding.currentDb.setFocusable(false);
        binding.currentDb.setFocusableInTouchMode(false);
        binding.currentDb.setAdapter(adapter);

        int id = getAllDb(this).indexOf(currentDb);
        if (id == -1) id = 0;
        binding.currentDb.setText(getAllDb(this).get(id), false);

        setText();

        ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        initApp(this);
                        initVariables();

                        ArrayAdapter<String> adapter_new = new ArrayAdapter<>(this, R.layout.dropdown_item, getAllDb(this));
                        binding.currentDb.setFocusable(false);
                        binding.currentDb.setFocusableInTouchMode(false);
                        binding.currentDb.setAdapter(adapter_new);

                        setText();
                    }
                }
        );

        binding.expenseAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add.class);
            intent.putExtra("isExpense", true);
            addLauncher.launch(intent);
        });

        binding.incomeAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add.class);
            intent.putExtra("isExpense", false);
            addLauncher.launch(intent);
        });

        binding.totalExpensesCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewData.class);
            intent.putExtra("isExpense", true);
            addLauncher.launch(intent);
        });

        binding.totalIncomesCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewData.class);
            intent.putExtra("isExpense", false);
            addLauncher.launch(intent);
        });

        binding.settings.setOnClickListener(v -> addLauncher.launch(new Intent(this, Settings.class)));

        binding.currentDb.setOnItemClickListener((parent, view, position, id1) -> {
            db.set(CURRENT_DB, parent.getItemAtPosition(position).toString());
            initVariables();
            binding.balans.setTextColor(Color.parseColor("#22C55E"));
            runOnUiThread(this::setText); // Update UI on the main thread
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                SyncDbOnExit.setActivity(MainActivity.this);

                OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(SyncDbOnExit.class).build();
                WorkManager.getInstance(MainActivity.this).enqueue(myWorkRequest);
                finish();
            }
        });
    }

    private void initVariables() {
        currentDb = currentDb(this);
        db = new Databases(this, MAIN_SETTINGS);
        expenses = new Databases(this, currentDb + EXPENSES);
        incomes = new Databases(this, currentDb + INCOMES);
        expensesPerAll = perAll(true);
        incomesPerAll = perAll(false);

        prefix = getPrefix(this);
    }

    private void setText() {
        binding.perAllDayMinusE.setText(String.format("%s%s", prefix, perDay(true)));
        binding.perAllDayAddE.setText(String.format("%s%s", prefix, perDay(false)));
        binding.perAllHistoryAddE.setText(String.format("%s%s", prefix, incomesPerAll));
        binding.perAllHistoryMinusE.setText(String.format("%s%s", prefix, expensesPerAll));
        binding.balans.setText(String.format("%s%s", prefix, getBalance()));

        if(isMinus()) binding.balans.setTextColor(Color.parseColor("#EF4444"));
    }

    private String perDay(boolean isExpenses) {
        BigDecimal total = BigDecimal.ZERO;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Map<String, String> allData;

        allData = isExpenses ? expenses.readAll() : incomes.readAll();

        // Format the current date
        String currentDateStr = formatter.format(new Date());

        for (Map.Entry<String, String> entry : allData.entrySet()) {
            try {
                Date currentDate = formatter.parse(currentDateStr);
                Date date1 = formatter.parse(entry.getKey().split(" ")[0]);

                int comparison = date1.compareTo(currentDate);
                if (comparison == 0) {
                    total = total.add(new BigDecimal(entry.getValue()));
                }
            } catch (ParseException ignored) {
            }
        }

        return String.valueOf(total);
    }

    private String perAll(boolean isExpenses) {
        BigDecimal total = BigDecimal.ZERO;

        Map<String, String> allData;
        allData = isExpenses ? expenses.readAll() : incomes.readAll();

        for (Map.Entry<String, String> entry : allData.entrySet()) {
            total = total.add(new BigDecimal(entry.getValue()));
        }

        return String.valueOf(total);
    }

    private String getBalance() {
        BigDecimal incomes = new BigDecimal(incomesPerAll);
        BigDecimal expenses = new BigDecimal(expensesPerAll);

        return incomes.subtract(expenses).toString();
    }

    private boolean isMinus() {
        BigDecimal incomes = new BigDecimal(incomesPerAll);
        BigDecimal expenses = new BigDecimal(expensesPerAll);

        return incomes.subtract(expenses).toString().contains("-");
    }

    @Override
    public void onUpdateReceived(String[] update) {
        String description = update[0];
        String link = update[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.updateTitle));
        builder.setMessage(getResources().getString(R.string.updateText) + description);
        builder.setPositiveButton("Update", (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link))));

        if (Long.parseLong(update[3]) - Long.parseLong(update[2]) == 1) {
            builder.setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss());
        }

        runOnUiThread(builder::show);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        binding.currentDb.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, getAllDb(this)));
    }
}