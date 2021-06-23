package com.oyl.controller;

import com.oyl.entity.Emp;
import com.oyl.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("emp")
@CrossOrigin
@Slf4j
public class EmpController {


    @Autowired
    private EmpService empService;

    @Value("${photo.dir}")
    private String realPath;


    //修改员工信息
    @PostMapping("update")//MultipartFile photo 这个参数名要与前端保持一致
    public Map<String, Object> update(Emp emp, MultipartFile photo) throws IOException {
        log.info("员工信息: [{}]", emp.toString());

        Map<String, Object> map = new HashMap<>();
        try {
            if (photo != null && photo.getSize() != 0) {
                log.info("头像信息: [{}]", photo.getOriginalFilename());
                //头像保存
                String newFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
                photo.transferTo(new File(realPath, newFileName));
                //设置头像地址
                emp.setPath(newFileName);
            }
            //保存员工信息
            empService.update(emp);
            map.put("state", true);
            map.put("msg", "员工信息保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "员工信息保存失败!");
        }
        return map;
    }

    //根据id查询员工信息实现
    @GetMapping("findOne")
    public Emp findOne(String id) {
        log.info("查询员工信息的id: [{}]", id);
        return empService.findOne(id);
    }

    //删除员工信息实现
    @GetMapping("delete")
    public Map<String, Object> delete(String id) {
        log.info("删除员工的id:[{}]", id);
        Map<String, Object> map = new HashMap<>();
        try {
            //删除员工头像
            Emp emp = empService.findOne(id);
            File file = new File(realPath, emp.getPath());
            if (file.exists()) file.delete();//删除头像
            //删除员工信息
            empService.delete(id);
            map.put("state", true);
            map.put("msg", "删除员工信息成功!");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "删除员工信息失败!");
        }
        return map;
    }


    //保存员工信息
    @PostMapping("save") //MultipartFile photo 这个参数名要与前端保持一致
    public Map<String, Object> save(Emp emp, MultipartFile photo) throws IOException {
        log.info("员工信息: [{}]", emp.toString());
        log.info("头像信息: [{}]", photo.getOriginalFilename());
        Map<String, Object> map = new HashMap<>();
        try {
        //头像保存
            //原始文件后缀名
            String name01 = FilenameUtils.getExtension(photo.getOriginalFilename());
            System.out.println("原始文件后缀名"+name01);
            //修改文件名
            String newFileName = UUID.randomUUID().toString() + "." + name01;//记得加点.
            System.out.println("新文件名"+newFileName);
            //文件上传  将上传过来的文件photo存储到路径为realPath的文件夹，文件命名为newFileName
            photo.transferTo(new File(realPath, newFileName));
            //emp对象（员工）设置员工头像存储的文件名
            emp.setPath(newFileName);//844c3c70-2de1-4b42-beb2-0820bb1f1c2e.jpg
            //保存员工信息
            empService.save(emp);
            map.put("state", true);
            map.put("msg", "员工信息保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "员工信息保存失败!");
        }
        return map;
    }

    //获取员工列表的方法
    @GetMapping("findAll")
    public List<Emp> findAll() {
        return empService.findAll();
    }

}
