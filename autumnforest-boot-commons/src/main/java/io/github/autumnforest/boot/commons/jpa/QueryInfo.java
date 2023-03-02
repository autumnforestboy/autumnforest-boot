package io.github.autumnforest.boot.commons.jpa;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create by 廖秋林 on 2021/7/31 18:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryInfo<T> {
    @ApiModelProperty(value = "选填, 排序字段. 空值，默认按主键升序，性能最优")
    private List<Sort> sortFields;
    @ApiModelProperty(value = "选填, 进行范围查询的字段")
    private List<Range> ranges;
    @ApiModelProperty(value = "选填, 空值也作为查询条件的字段")
    private List<String> nullAble;
    @ApiModelProperty(value = "选填, 进行模糊查询的字段")
    private List<String> fuzzy;
    @ApiModelProperty(value = "查询条件，会忽略空值字段", required = true)
    private T fields;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Sort {
        @ApiModelProperty(value = "排序字段")
        private String field;
        @ApiModelProperty(value = "是否升序")
        private Boolean ascend;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Range {
        @ApiModelProperty(value = "范围查找字段")
        private String filed;
        private Value startValue;
        private Value endValue;

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class Value {
            private boolean equal;
            private Object value;
        }
    }
}