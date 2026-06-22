package com.aide.common.util;


import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author mazg
 * @description 分页查询参数
 * @date 2026/6/22
 * @date 20:32
 */
@Data
public class BasePageQuery {

    /**
     * 当前页码，默认第 1 页
     */
    @Min(value = 1, message = "页码最小值为1")
    private Integer pageNum = 1;

    /**
     * 每页条数，默认 10 条，最大限制 100 条（防止恶意传入超大数值导致 DB 压力）
     */
    @Min(value = 1, message = "每页条数最小值为1")
    @Max(value = 100, message = "每页条数最大不能超过100")
    private Integer pageSize = 10;
}