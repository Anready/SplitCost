package com.codersanx.splitcost.utils.adapters;

import java.math.BigDecimal;

public class Category {
    private final String title, description;
    private final BigDecimal categoryValue, totalValue;

    public Category(String title, String description, BigDecimal currentExpense, BigDecimal maxExpense) {
        this.title = title;
        this.description = description;
        this.categoryValue = currentExpense;
        this.totalValue = maxExpense;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getCategoryValue() {
        return categoryValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }
}

