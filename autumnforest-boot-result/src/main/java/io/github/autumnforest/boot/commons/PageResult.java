package io.github.autumnforest.boot.commons;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer currentPage;
    /**
     * 总页码
     */
    private Integer totalPage;
    /**
     * 符合条件总数据条数
     */
    private Integer totalCount;
    /**
     * 当前页码数据
     */
    private List<T> items;

}

