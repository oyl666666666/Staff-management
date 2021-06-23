package com.oyl.service;

import com.oyl.dao.UserDAO;
import com.oyl.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public User login(User user) {
        //1.根据用户输入用户名进行查询
        User userDB = userDAO.findByUserName(user.getUsername());
        //  判断用户是否为空!ObjectUtils.isEmpty(userDB)
        if(!ObjectUtils.isEmpty(userDB)){
            //不为空则比较密码
            //2.比较密码
            if (userDB.getPassword().equals(user.getPassword())) {
                return  userDB;
            }else{
                //密码错误
                //异常就相当于返回
                throw new RuntimeException("密码输入不正确!");
            }
        }else{
            //没有此用户
            //异常就相当于返回
            throw  new  RuntimeException("用户名输入错误!");
        }
    }

    @Override
    public void register(User user) {
        //0.根据用户输入用户名判断用户是否存在
        User userDB = userDAO.findByUserName(user.getUsername());
        if(userDB==null){
            //1.生成用户状态
            user.setStatus("已激活");
            //2.设置用户注册时间
            user.setRegisterTime(new Date());
            //3.调用DAO
            userDAO.save(user);
        }else{
            throw new RuntimeException("用户名已存在!");
        }
    }
}
