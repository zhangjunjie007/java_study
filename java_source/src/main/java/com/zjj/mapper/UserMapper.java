package com.zjj.mapper;

import com.zjj.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
   User getUserById(int userId);

   void updateUserById(int userId);
}
