package com.notebook.note_back.common.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

public class LoadUtil {

    //上传图片
    public static String uploadImg(MultipartFile multipartFile, String basePath) {
        // 获取绝对路径
        File f = new File(basePath);
        // 如果不存在，直接创建
        if (!f.exists()) {
            f.mkdirs();
        }
        // 获取原图片名称
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID生成文件名
        String filename = UUID.randomUUID() + suffix;
        // 拼接的图片路径
        String filepath = basePath + filename;
        File file = new File(filepath);
        // 上传图片
        try {
            multipartFile.transferTo(file);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filepath;
    }

    // 回显图片
    public static void showImg(String imgUrl, HttpServletResponse response) {
        // 获取图片的当前路径 放入读
        FileInputStream fis = null;
        // 用response 获取一个写对象的流
        ServletOutputStream os = null;
        try {
            fis = new FileInputStream(imgUrl);
            os = response.getOutputStream();
            // 提高读写的速度
            byte[] b = new byte[1024];
            // 边读边写
            while (fis.read(b) != -1) {
                os.write(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 下载
    public static void downLoad(String filePath, HttpServletRequest request, HttpServletResponse response) {
        //设置文件的MiMe类型
        response.setContentType(request.getSession().getServletContext().getMimeType(filePath));
        //设置content-disposition
        response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
        //读取目标文件，通过response将目标文件写到客户
        try {
            // 读取文件
            InputStream in = new FileInputStream(filePath);
            OutputStream out = response.getOutputStream();
            //写文件
            byte[] b = new byte[1024];
            while (in.read(b) != -1) {
                out.write(b);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
