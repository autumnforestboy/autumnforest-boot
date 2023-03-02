package io.github.autumnforest.boot.commons.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@Slf4j
public class TableSchema {
    private final List<TableColumn> columns = new LinkedList<>();
    private final Map<String, TableColumn> columnMap = new HashMap<>();

    public void addColumn(TableColumn tableColumn) {
        columns.add(tableColumn);
        columnMap.put(tableColumn.getClassColumnName(), tableColumn);
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public Class getClassByName(String variable) {
        return columnMap.get(variable).getColumnType();
    }

    public void check(List<QueryInfo.Range> ranges) throws Exception {
        if (ranges != null && ranges.size() > 0) {
            for (QueryInfo.Range range :
                    ranges) {
                String filed = range.getFiled();
                if (columnMap.get(filed) == null) {
                    throw new Exception("范围查找字段不存在:" + filed);
                }
            }
        }
    }

    public void sort() {
        columns.sort((o1, o2) -> {
            ColumnOrder annotation1 = o1.getFiled().getDeclaredAnnotation(ColumnOrder.class);
            int order1 = annotation1 == null ? 100 : annotation1.value();
            ColumnOrder annotation2 = o2.getFiled().getDeclaredAnnotation(ColumnOrder.class);
            int order2 = annotation2 == null ? 100 : annotation2.value();
            return order1 - order2;
        });
    }


}
