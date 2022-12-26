package com.zjj.controller;

import com.zjj.domain.CommonRespo;
import com.zjj.service.UserService;
import com.zjj.util.CommonRespoUtil;
import lombok.extern.slf4j.Slf4j;
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
    @RequestMapping(value = "",method = {RequestMethod.GET,RequestMethod.POST})
    public CommonRespo<String> testTransaction(@RequestParam int param_1){
        log.info("进入到方法【{}】",param_1);
        userService.updateUserStatus(param_1);
        return CommonRespoUtil.isOk("测试一下");
    }
}
