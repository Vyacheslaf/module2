package com.epam.esm.util;

public enum SortBy {
    NAME("gc.name"), DATE("gc.create_date");

    private String columnName;

    SortBy(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
