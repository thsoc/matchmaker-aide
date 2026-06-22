package com.aide.common.util;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mazg
 * @description 分页工具
 * @date 2026/6/22
 * @date 20:34
 */
public class PageUtil {

    /**
     * 将通用分页参数转换为 MyBatis-Plus 的 Page 对象
     */
    public static <T> Page<T> buildPage(BasePageQuery query) {
        return new Page<>(query.getPageNum(), query.getPageSize());
    }
}
