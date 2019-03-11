package com.example.demo;

import com.example.demo.asrdemo.AsrMain;
import com.example.demo.common.DemoException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
public class Testcontroller {
    @RequestMapping("/speech")
    public @ResponseBody
    String speech(HttpServletRequest hrq, HttpServletResponse hsr) throws IOException, DemoException {
        hrq.setCharacterEncoding("UTF-8");
        MultipartHttpServletRequest mul = (MultipartHttpServletRequest)hrq;
        MultipartFile file = mul.getFile("audioData");
        AsrMain demo = new AsrMain(file);
        // 填写下面信息
        String result = demo.run();
        System.out.println("UTF-8========测试！");
        System.out.println("result8========"+result);
        return result;
    }
}
