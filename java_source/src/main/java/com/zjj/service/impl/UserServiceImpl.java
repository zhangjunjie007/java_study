package com.zjj.service.impl;

import com.zjj.domain.User;
import com.zjj.mapper.UserMapper;
import com.zjj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;
    @Override
    public void updateUserStatus(int id) {
        //查询id对应的user值
        User user = userMapper.getUserById(id);
        log.info("查询用户【{}】的返回值为【{}】",id,user);
        //修改user的值为一个报错的
        //userMapper.updateUserById(id);
    }
}
