package io.github.autumnforest.boot.commons;

import lombok.Data;

@Data
public class PageParam<T> {
    /**
     * 每页数据条数
     */
    private Integer countPerPage;
    /**
     *  查询页码，从1开始
     */
    private Integer currentPage;
    /**
     * 查询参数
     */
    private T queryInfo;
}

