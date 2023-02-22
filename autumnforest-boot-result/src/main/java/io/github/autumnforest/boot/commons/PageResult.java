package io.github.autumnforest.boot.commons;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer currentPageNum;
    /**
     * 总页码
     */
    private Integer totalPageNum;
    /**
     * 当前页码数据
     */
    private List<T> items;
}

