package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是临时文件，执行完即消失
        //为防止重名，所有UUID类给初始名
        String name = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = name+suffix;

        File file1 = new File(basePath);
        if (!file1.exists()){
            file1.mkdirs();
        }
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse res){
        try {
            //输入流读取本地缓存图片
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));

            //response输入流，把本地文件输入到浏览器客户端上
            ServletOutputStream outputStream = res.getOutputStream();

            //设置返回格式
            res.setContentType("image/jpeg");
            //创建缓存
            int len = 0;
            byte[] bytes = new byte[1024];

            //开始读写输入
            while ((len = inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                //输入完毕，刷新
                outputStream.flush();
            }

            //关闭资源
            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
