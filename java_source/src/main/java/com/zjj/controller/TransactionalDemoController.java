package com.zjj.controller;

import com.zjj.domain.CommonRespo;
import com.zjj.service.UserService;
import com.zjj.util.CommonRespoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试编程式事务的使用
 */
@RestController
@RequestMapping("/transaction/test")
@Slf4j
public class TransactionalDemoController {
    @Resource
    private UserService userService ;

    @Resource
    PlatformTransactionManager transactionManager ;

    @RequestMapping(value = "",method = {RequestMethod.GET,RequestMethod.POST})
    public CommonRespo<String> testTransaction(@RequestParam int param_1,@RequestParam int param_2){
        log.info("进入到方法【{}】",param_1);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //事务的传播属性
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            //正常修改
            userService.updateUserStatus(14,0);

            //状态改为10 会报错  后台status为char（1）
            userService.updateUserStatus(15,10);
            transactionManager.commit(status);
        } catch (Exception e) {
            System.out.println("报错了:"+e.getMessage());
            log.error("执行更新报错"+e.getMessage());
            //报错以后这里会回滚
            transactionManager.rollback(status);
        }

        return CommonRespoUtil.isOk("测试一下");
    }
}
