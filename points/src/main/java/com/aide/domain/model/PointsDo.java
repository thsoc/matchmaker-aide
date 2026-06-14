package com.aide.domain.model;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 积分领域对象
 * @date 2026/6/14
 * @date 20:48
 */
@Getter
@Builder
@Data
public class PointsDo {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 积分类型，1-会员购买
     */
    private Integer pointsType;
    /**
     * 积分
     */
    private Integer points;

    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 版本号
     */
    @Version
    private Integer version;

    public void initFromAddPoints() {
//        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.createBy = this.userId.toString();
        this.updateBy = this.userId.toString();
    }
}
