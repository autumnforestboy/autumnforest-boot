package io.github.autumnforest.boot.commons.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableColumn {
    private String classColumnName;
    private String tableColumnName;
    private Object columnValue;
    private Class<?> columnType;
    private Field filed;

    public TableColumn(TableColumn tableColumn) {
        this.setTableColumnName(tableColumn.getTableColumnName());
        this.setClassColumnName(tableColumn.getClassColumnName());
        this.setColumnType(tableColumn.getColumnType());
        this.setColumnValue(tableColumn.getColumnValue());
    }

}
