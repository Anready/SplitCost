package com.codersanx.splitcost.view.chart;

import static com.codersanx.splitcost.utils.Utils.getPrefix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codersanx.splitcost.R;
import com.codersanx.splitcost.databinding.ActivityChartBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Chart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityChartBinding binding = ActivityChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String[] items = intent.getStringArrayExtra("list_items");
        PieChart pieChart = binding.chart;

        findViewById(R.id.back).setOnClickListener(view -> finish());

        Map<String, Float> dictionary = new HashMap<>();

        for (String item : items) {
            String[] splitItem = item.split(", ");

            if (dictionary.containsKey(splitItem[1])) {
                float value = dictionary.get(splitItem[1]);
                dictionary.put(splitItem[1], Float.parseFloat(splitItem[2]) + value);
            } else {
                dictionary.put(splitItem[1], Float.parseFloat(splitItem[2]));
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (String key : dictionary.keySet())
            entries.add(new PieEntry(dictionary.get(key) + 0F, key));

        PieDataSet dataSet = getPieDataSet(entries);

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setEntryLabelColor(ContextCompat.getColor(this, R.color.light_blue_900));
        pieChart.setExtraOffsets(30, 30, 30, 30);

        PieData data = new PieData(dataSet);

        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setTextSize(12f);
        legend.setFormSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setTextColor(ContextCompat.getColor(this, R.color.calcButtonText));

        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            PieEntry entry = entries.get(i);
            String label;
            String chartValue = String.valueOf(entry.getValue());
            if (chartValue.endsWith(".0")){
                label = String.format(Locale.getDefault(), "%s - %s (%.1f%%)", entry.getLabel(), chartValue.replace(".0", "") +  getPrefix(this), entry.getY() / data.getYValueSum() * 100);
            } else {
                label = String.format(Locale.getDefault(), "%s - %.2f%s (%.1f%%)", entry.getLabel(), entry.getValue(), getPrefix(this), entry.getY() / data.getYValueSum() * 100);
            }

            LegendEntry legendEntry = new LegendEntry();
            legendEntry.label = label;
            legendEntries.add(legendEntry);
        }

        legend.setCustom(legendEntries);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    @NonNull
    private PieDataSet getPieDataSet(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.calcButtonText));
        dataSet.setValueTextSize(12f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(20.f);
        dataSet.setValueLinePart1Length(1.3f);
        dataSet.setValueLinePart2Length(.3f);
        dataSet.setValueLineColor(ContextCompat.getColor(this, R.color.calcButtonText));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.calcButtonText));
        DecimalValueFormatter formatter = new DecimalValueFormatter(this);
        dataSet.setValueFormatter(formatter);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        return dataSet;
    }
}

class DecimalValueFormatter extends ValueFormatter {

    private final Context context;

    public DecimalValueFormatter(Context context) {
        this.context = context;
    }

    @Override
    public String getFormattedValue(float value) {
        String item = String.format(Locale.getDefault(), "%.2f", value);

        if (item.endsWith(",00")) {
            return item.replace(",00", "") + getPrefix(context);
        } else if (item.endsWith(".00")) {
            return item.replace(".00", "") + getPrefix(context);
        }

        return String.format(Locale.getDefault(), "%.2f", value) + getPrefix(context);
    }
}