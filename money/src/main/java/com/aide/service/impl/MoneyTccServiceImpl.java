//package com.aide.service.impl;
//
//import com.aide.common.exception.MoneyException;
//import com.aide.config.DynamicDataSourceManager;
//import com.aide.config.DynamicDataSourceRegistry;
//import com.aide.domain.service.MoneyDomainService;
//import com.aide.service.MoneyTccService;
//import io.seata.core.context.RootContext;
//import io.seata.rm.tcc.api.BusinessActionContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.sql.DataSource;
//import java.math.BigDecimal;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author mazg
// * @description Tcc分布式事务扣除金额
// * @date 2026/6/9
// * @date 19:40
// */
//@Service
//@Slf4j
//public class MoneyTccServiceImpl implements MoneyTccService {
//    @Autowired
//    private MoneyDomainService moneyDomainService;
////    // 动态数据源管理器
////    @Autowired
////    private DynamicDataSourceManager dynamicDataSourceManager;
////
////    // 存储每个分片的数据源连接
////    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();
//
//    /**
//     * @author mazg
//     * @description Try 阶段：冻结资金
//     * @date 19:48 2026/6/9
//     * @return
//     **/
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean deductBalance(BusinessActionContext context, Long userId,
//                                 BigDecimal amount, String description) throws MoneyException {
//        log.info(">>> try START xid={}", RootContext.getXID());
//
//        log.info("Try阶段：准备冻结资金, userId={}, amount={}, description={}", userId, amount, description);
//
////        // 1. 根据用户ID计算分片键
////        String shardKey = calculateShardKey(userId);
////
////        // 2. 获取对应的数据源
////        DataSource dataSource = dynamicDataSourceManager.getDataSource(shardKey);
////        if (dataSource == null) {
////            throw new RuntimeException("获取数据源失败，分片键：" + shardKey);
////        }
////
////        Connection conn = null;
////        PreparedStatement pstmt = null;
////        ResultSet rs = null;
//
//        // 框架会自动在此处向 tcc_fence_log 插入一条 status=STATUS_TRIED 的记录
//        // 如果发生主键冲突或 suspended 状态，框架会自动拦截并抛出异常
//
//        int rows = moneyDomainService.freezeMoney(userId, amount);
//        if (rows == 0) {
//            throw MoneyException.moneyDeduceError(userId);
//        }
//        log.info(">>> try end xid={}", RootContext.getXID());
//        return true;
//    }
//
//    /**
//     * @author mazg
//     * @description Confirm 阶段：真正扣除冻结金额
//     * @date 19:49 2026/6/9
//     * @return
//     **/
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean confirmDeduct(BusinessActionContext context) {
//        log.info(">>> confirm START xid={}", RootContext.getXID());
//        Long userId = (Long) context.getActionContext("userId");
//        BigDecimal amount = (BigDecimal) context.getActionContext("amount");
//
//        log.info("Confirm阶段：确认扣款, userId={}, amount={}", userId, amount);
//
//        // 框架会自动校验 tcc_fence_log 的状态
//        // 若已提交过，直接返回成功（保证幂等）；否则更新状态为 COMMITTED 并执行业务
//        moneyDomainService.confirmFreeze(userId, amount);
//        log.info(">>> confirm end xid={}", RootContext.getXID());
//        return true;
//    }
//
//    /**
//     * @author mazg
//     * @description Cancel 阶段：解冻资金，恢复可用余额
//     * @date 19:49 2026/6/9
//     * @return
//     **/
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean cancelDeduct(BusinessActionContext context) {
//        log.info(">>> cancel START xid={}", RootContext.getXID());
//        Long userId = (Long) context.getActionContext("userId");
//        BigDecimal amount = (BigDecimal) context.getActionContext("amount");
//
//        log.info("Cancel阶段：取消冻结, userId={}, amount={}", userId, amount);
//
//        // 框架会自动处理：
//        // 1. 空回滚：若无 Try 记录，插入 STATUS_SUSPENDED 并跳过业务回滚
//        // 2. 正常回滚：更新状态为 ROLLBACKED 并执行业务补偿
//        moneyDomainService.unfreezeMoney(userId, amount);
//        log.info(">>> cancel end xid={}", RootContext.getXID());
//        return true;
//    }
//}
