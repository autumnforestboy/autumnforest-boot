package io.github.autumnforest.boot.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "分页数据")
public class PageResult<T> {
    @ApiModelProperty(value = "总页数")
    private int totalPages;
    @ApiModelProperty(value = "总条数")
    private long totalElement;
    @ApiModelProperty(value = "当前页码，正整数")
    private int currentPage;
    @ApiModelProperty(value = "当前页的数据List")
    private List<T> rows;

}

