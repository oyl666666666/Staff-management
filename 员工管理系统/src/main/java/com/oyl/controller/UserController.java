package com.oyl.controller;

import com.oyl.entity.User;
import com.oyl.service.UserService;
import com.oyl.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin //允许跨域
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 用来处理用户登录
     */
    @PostMapping("login")         //@RequestBody 将json转为对象
    public Map<String,Object> login(@RequestBody User user){
        log.info("当前登录用户的信息: [{}]",user.toString());
        Map<String, Object> map =  new HashMap<>();
        try {
            User userDB = userService.login(user);
            map.put("state",true);
            map.put("msg","登录成功!");
            map.put("user",userDB);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }

    /**
     * 用来处理用户注册方法
     */
    @PostMapping("register")
    public Map<String, Object> register(@RequestBody User user, String code, HttpServletRequest request) {
        log.info("用户信息:[{}]",user.toString());
        log.info("用户输入的验证码信息:[{}]",code);
        Map<String, Object> map = new HashMap<>();
        try {
            //获取验证码（）  因为验证码是放入servletContext作用域中的，全局作用，所以可以取到
            String key = (String) request.getServletContext().getAttribute("code");
            if (key.equalsIgnoreCase(code)) {
                //1.调用业务方法
                userService.register(user);
                map.put("state", true);
                map.put("msg", "提示: 注册成功!");
            } else {
                throw new RuntimeException("验证码出现错误!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "提示:"+e.getMessage());
        }
        return map;
    }

    /**
     * 生成验证码图片
     */
    @GetMapping("getImage")
    public String getImageCode(HttpServletRequest request) throws IOException {
        //1.使用验证码工具类VerifyCodeUtils生成验证码   4位的验证码
        String code = VerifyCodeUtils.generateVerifyCode(4);
        //2.将验证码放入servletContext作用域   也可以放到redis数据库里面   前后端分离就没有session了，不要放session里面了，要注意
        request.getServletContext().setAttribute("code", code);
        //3.将图片转为base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //宽120  高30  字节数组输出流对象   生成验证码后的String
        VerifyCodeUtils.outputImage(120, 30, byteArrayOutputStream, code);  //生成验证码图片
        byte[] img=byteArrayOutputStream.toByteArray();
        String img_base64 = Base64Utils.encodeToString(img);//将图片转为base64编码格式
        //前端img标签显示base64格式的图片  <img src="data:image/png;base64,这里加base64编码" alt="">
        //https://blog.csdn.net/kukudehui/article/details/80409522   看这个
        return "data:image/png;base64," + img_base64;
    }
}
