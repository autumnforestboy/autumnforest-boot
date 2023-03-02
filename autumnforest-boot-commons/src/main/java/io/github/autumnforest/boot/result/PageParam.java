package io.github.autumnforest.boot.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description = "分页查询请求信息")
@Data
public class PageParam<T> {
    @ApiModelProperty(value = "页码，正整数或-1（表示不分页，查询所有）", required = true, example = "1")
    private Integer currentPage = 1;
    @ApiModelProperty(value = "每页大小", required = true, example = "10")
    private Integer pageSize = 10;
    /**
     * 查询参数
     */
    @ApiModelProperty(value = "查询参数", required = true)
    private T queryInfo;
}

